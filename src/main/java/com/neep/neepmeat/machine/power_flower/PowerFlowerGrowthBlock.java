package com.neep.neepmeat.machine.power_flower;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class PowerFlowerGrowthBlock extends BaseBlock implements PowerFlower
{
    public static final IntProperty GROWTH = Properties.AGE_1;

    private final VoxelShape fullShape = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
    private final VoxelShape topShape = Block.createCuboidShape(0, 0, 0, 16, 14, 16);

    public PowerFlowerGrowthBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());
        this.setDefaultState(getDefaultState().with(GROWTH, 0));
    }

    @Override
    public TagKey<Block> getPreferredTool()
    {
        return BlockTags.AXE_MINEABLE;
    }

    @Override
    public boolean autoGenDrop()
    {
        return false;
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context)
    {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return state.get(GROWTH) == 0 ? fullShape : topShape;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
//        return validBlock(world.getBlockState(pos.down()).getBlock()) && super.canPlaceAt(state, world, pos);
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
//        if (state.get(GROWTH) == 0 && sourcePos.equals(pos.down()) && !validBlock(world.getBlockState(sourcePos).getBlock()))
//        {
//            world.breakBlock(pos, true);
//        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getStackInHand(hand);
        int tries = triesForItem(stack);

        if (tries > 0)
        {
            if (!world.isClient())
            {
                for (int i = 0; i < tries; ++i)
                {
                    grow(world, pos, state);
                }
            }
            if (!player.isCreative())
            {
                stack.decrement(1);
                player.setStackInHand(hand, stack);
            }

            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public static int triesForItem(ItemStack stack)
    {
        if (stack.isIn(NMTags.CHARNEL_COMPACTOR))
            return 2;

        if (stack.isOf(Items.BONE_MEAL))
            return 1;

        return 0;
    }

    public void grow(World world, BlockPos pos, BlockState state)
    {
        Random random = world.getRandom();
        Direction direction = Direction.values()[random.nextInt(5) + 1];
        BlockPos growPos = pos.offset(direction);

        if (canGrowTo(world, growPos))
            world.setBlockState(growPos, getState(world, growPos));
    }

    private boolean canGrowTo(World world, BlockPos pos)
    {
        BlockState downState = world.getBlockState(pos.down());
        BlockState nextState = world.getBlockState(pos);
        return nextState.isAir() && (downState.isOf(this) || !downState.isAir());
    }

    public BlockState getState(WorldAccess world, BlockPos pos)
    {
        boolean upAir = world.isAir(pos.up());
        boolean downThis = world.getBlockState(pos.down()).getBlock() instanceof PowerFlower;

        return getDefaultState().with(GROWTH, upAir && downThis ? 1 : 0);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        return getState(world, pos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return getState(ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
//        return state.get(GROWTH) == 0;
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
//        if (state.get(GROWTH) == 0 && validBlock(world.getBlockState(pos.down()).getBlock()))
//        {
//            world.setBlockState(pos.down(), state.with(GROWTH, 1));
//            world.setBlockState(pos, Blocks.AIR.getDefaultState());
//        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(GROWTH);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextInt(10) == 0)
        {
            world.addParticle(ParticleTypes.MYCELIUM,
                    pos.getX() + random.nextFloat() * 1.4 - 0.2,
                    pos.getY() + random.nextFloat() * 1.4 - 0.2,
                    pos.getZ() + random.nextFloat() * 1.4 - 0.2,
                    random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5);
        }
    }
}
