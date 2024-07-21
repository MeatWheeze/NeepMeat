package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.content_detector.InventoryDetectorBlock;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class EjectorBlock extends BaseFacingBlock implements BlockEntityProvider, ItemPipe
{
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public EjectorBlock(String registryName, ItemSettings itemSettings, FabricBlockSettings settings)
    {
        super(ctx, itemSettings, settings.nonOpaque().solidBlock(InventoryDetectorBlock::never));
        setDefaultState(getDefaultState().with(ACTIVE, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.EJECTOR.instantiate(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.EJECTOR, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        boolean redstone = context.getWorld().isReceivingRedstonePower(context.getBlockPos());
        BlockState superState = super.getPlacementState(context);

        if (superState == null)
            return null;

        // Not inverted by default
        return superState.with(ACTIVE, redstone);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.getBlockEntity(pos) instanceof EjectorBlockEntity be && !world.isClient())
        {
            be.updatePowered(world.isReceivingRedstonePower(pos));
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!oldState.isOf(this) && world.getBlockEntity(pos) instanceof EjectorBlockEntity be && !world.isClient())
        {
            be.updatePowered(world.isReceivingRedstonePower(pos));
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be)
            MeatlibStorageUtil.scatterAmount(world, pos, be.stored);

        if (world instanceof ServerWorld serverWorld)
            onChanged(pos, serverWorld);

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof EjectorBlockEntity be)
        {
            if (player.isSneaking())
            {
                if (!world.isClient())
                {
                    be.changeMode();
                    world.playSound(null, pos, NMSounds.CLICK, SoundCategory.BLOCKS, 1, 1);
                }

                return ActionResult.SUCCESS;
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ACTIVE);
    }

    // TODO: make this do things
    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return direction.equals(state.get(FACING));
    }

    @Override
    public EnumSet<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        Direction facing = state.get(FACING);
        if (forbidden.test(facing) && forbidden.test(facing.getOpposite()))
            return EnumSet.noneOf(Direction.class);
        else if (!forbidden.test(facing))
            return EnumSet.of(facing.getOpposite());
        else
            return EnumSet.of(facing);
    }
}
