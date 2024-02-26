package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemPipeBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.MergePipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class MergePipeBlock extends ItemPipeBlock
{
    public static final DirectionProperty FACING = DirectionProperty.of("facing");

    public MergePipeBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
        this.setDefaultState(super.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape shape = getCentreShape();
        Direction facing = state.get(FACING);
        for (Direction direction : Direction.values())
        {
            if (state.get(DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
            {
                shape = VoxelShapes.union(shape, DIR_SHAPES.get(direction));
            }
        }
        shape = VoxelShapes.union(shape, DIR_SHAPES.get(facing));
//        shape = VoxelShapes.union(shape, DIR_SHAPES.get(facing.getOpposite()));
        return shape;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState state = this.getConnectedState(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
        return state.with(FACING, ctx.getPlayer().isSneaking() ? ctx.getPlayerLookDirection().getOpposite() : ctx.getPlayerLookDirection());
    }

    @Override
    protected BlockState getConnectedState(BlockView world, BlockState state, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            PipeConnectionType property = state.get(DIR_TO_CONNECTION.get(direction));
            if (property == PipeConnectionType.SIDE)
                continue;

            BlockPos adjPos = pos.offset(direction);
            BlockState adjState = world.getBlockState(adjPos);
            boolean adjConnect = canConnectTo(adjState, direction.getOpposite(), (World) world, pos);
            boolean connect = connectInDirection(world, pos, state, direction);
            state = state.with(DIR_TO_CONNECTION.get(direction), adjConnect && connect ? PipeConnectionType.SIDE : PipeConnectionType.NONE);
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        PipeConnectionType type = state.get(DIR_TO_CONNECTION.get(direction));
        Direction facing = state.get(FACING);
        boolean forced = type == PipeConnectionType.FORCED;

        boolean adjConnect = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);
        boolean connect = connectInDirection(world, pos, state, direction);
        boolean connection = adjConnect && connect;
        if (!world.isClient() && !(neighborState.getBlock() instanceof ItemPipe) && direction != facing)
        {
            connection = connection || (canConnectApi((World) world, pos, state, direction));
        }

        // Check if neighbour is forced
        if (neighborState.getBlock() instanceof ItemPipeBlock)
        {
            forced = forced || neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.FORCED;
        }

        PipeConnectionType connection1 = forced
                ? PipeConnectionType.NONE
                : connection ? PipeConnectionType.SIDE : PipeConnectionType.NONE;

        // I don't know what this bit was for.
        return state.with(DIR_TO_CONNECTION.get(direction), connection1);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
//        Vec3d hitPos = hit.getPos();
//        Direction side = hit.getSide();
//        Direction useDirection = getUseDirection(side, pos, hitPos);
//        if (useDirection == state.get(FACING).getOpposite())
//        {
//            return ActionResult.SUCCESS;
//        }
        return ActionResult.PASS;
    }

    @Override
    public boolean canConnectTo(BlockState toState, Direction toFace, World world, BlockPos toPos)
    {
        if (toState.getBlock() instanceof ItemPipe pipe)
        {
            return pipe.connectInDirection(world, toPos, toState, toFace);
        }
        return false;
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
//        return direction != state.get(FACING).getOpposite();
        return true;
    }

    @Override
    public Direction getOutputDirection(ItemInPipe item, BlockState state, World world, Direction in)
    {
        return state.get(MergePipeBlock.FACING);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public Set<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        Set<Direction> set = new HashSet<>();
        for (Direction direction : Direction.values())
        {
            if (state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected()
                    && forbidden.test(direction))
            {
                set.add(direction);
            }
            set.add(state.get(FACING));
        }
        return set;
    }

    @Override
    public boolean singleOutput()
    {
        return true;
    }

    @Override
    public boolean canItemEnter(ResourceAmount<ItemVariant> item, World world, BlockPos pos, BlockState state, Direction inFace)
    {
        return super.canItemEnter(item, world, pos, state, inFace) && inFace != state.get(FACING);
    }

    @Override
    public boolean canItemLeave(ResourceAmount<ItemVariant> item, World world, BlockPos pos, BlockState state, Direction outFace)
    {
        return super.canItemLeave(item, world, pos, state, outFace) && outFace == state.get(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MergePipeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.MERGE_ITEM_PIPE, ItemPipeBlockEntity::serverTick, null, world);
    }
}
