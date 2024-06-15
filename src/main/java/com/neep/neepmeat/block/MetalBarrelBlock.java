package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.block.entity.MetalBarrelBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class MetalBarrelBlock extends BarrelBlock implements MeatlibBlock
{
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    private final String name;

    public MetalBarrelBlock(String name, Settings settings)
    {
        super(settings);
        this.name = name;
        ItemRegistry.queue(new BaseBlockItem(this, name, ItemSettings.block()));
        setDefaultState(getStateManager().getDefaultState().with(TYPE, Type.SINGLE).with(OPEN, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.METAL_BARREL.instantiate(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof MetalBarrelBlockEntity be)
        {
            be.setCustomName(itemStack.getName());
        }
    }

    public static @Nullable MetalBarrelBlockEntity getOther(World world, BlockPos pos, BlockState state)
    {
        Type type = state.get(TYPE);
        Direction facing = state.get(FACING);

        if (type == Type.SINGLE)
            return null;


        return world.getBlockEntity(
                    pos.offset(type == Type.TOP ? facing.getOpposite() : facing),
                    NMBlockEntities.METAL_BARREL)
                .orElse(null);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.isClient)
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MetalBarrelBlockEntity be)
        {
            @Nullable MetalBarrelBlockEntity be2 = getOther(world, pos, state);
            if (state.get(TYPE) == Type.TOP)
            {
                player.openHandledScreen(be.openHandler(player, world, pos, state, be, be2));
                return ActionResult.CONSUME;
            }
            else if (state.get(TYPE) == Type.BOTTOM)
            {
                player.openHandledScreen(be.openHandler(player, world, pos, state, be2, be));
                return ActionResult.CONSUME;
            }

            player.openHandledScreen(be.openHandler(player, world, pos, state, be, null));
        }

        return ActionResult.CONSUME;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        Direction side = ctx.getSide();
        BlockState state = this.getDefaultState().with(FACING, ctx.getSide());

        BlockState offsetState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(side.getOpposite()));

        if (offsetState.isOf(this)
                && offsetState.get(TYPE) == Type.SINGLE
                && offsetState.get(FACING) == side)
        {
            return state.with(TYPE, Type.TOP);
        }

        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        BlockState superState = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        Direction facing = state.get(FACING);
        Type prevType = state.get(TYPE);

        BlockState frontState = world.getBlockState(pos.offset(facing));
        BlockState backState = world.getBlockState(pos.offset(facing.getOpposite()));

        // Become bottom if previously single
        if (frontState.isOf(this)
            && prevType == Type.SINGLE
            && frontState.get(TYPE) == Type.TOP)
        {
            return superState.with(TYPE, Type.BOTTOM);
        }
        else if ((!frontState.isOf(this)
                || frontState.get(TYPE) != Type.TOP)
                && prevType == Type.BOTTOM)
        {
            return superState.with(TYPE, Type.SINGLE);
        }
        else if ((!backState.isOf(this)
                || backState.get(FACING) != facing)
                && prevType == Type.TOP)
        {
            return superState.with(TYPE, Type.SINGLE);
        }

        return superState;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(TYPE);
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }

    public enum Type implements StringIdentifiable
    {
        TOP,
        BOTTOM,
        SINGLE;

        @Override
        public String asString()
        {
            return this.toString().toLowerCase();
        }
    }
}
