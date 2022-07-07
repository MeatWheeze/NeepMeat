package com.neep.neepmeat.block.item_transport;

import com.neep.neepmeat.block.pipe.IItemPipe;
import com.neep.neepmeat.blockentity.pipe.MergePipeBlockEntity;
import com.neep.neepmeat.blockentity.pipe.PneumaticPipeBlockEntity;
import com.neep.neepmeat.fluid_transfer.PipeConnectionType;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUitls;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class MergePipeBlock extends PneumaticTubeBlock
{
    public static final DirectionProperty FACING = DirectionProperty.of("facing");

    public MergePipeBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
        this.setDefaultState(super.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape shape = Block.createCuboidShape(4, 4, 4, 12, 12, 12);
        Direction facing = state.get(FACING);
        for (Direction direction : Direction.values())
        {
            if (state.get(DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
            {
                shape = VoxelShapes.union(shape, DIR_SHAPES.get(direction));
            }
        }
        shape = VoxelShapes.union(shape, DIR_SHAPES.get(facing));
        shape = VoxelShapes.union(shape, DIR_SHAPES.get(facing.getOpposite()));
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
        if (!world.isClient() && !(neighborState.getBlock() instanceof IItemPipe) && direction == facing)
        {
            connection = connection || (canConnectApi((World) world, pos, state, direction));
        }

        // Check if neighbour is forced
        if (neighborState.getBlock() instanceof PneumaticTubeBlock)
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
        Vec3d hitPos = hit.getPos();
        Direction side = hit.getSide();
        Direction useDirection = getUseDirection(side, pos, hitPos);
        if (useDirection == state.get(FACING).getOpposite())
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction direction, World world, BlockPos pos)
    {
        if (state.getBlock() instanceof IItemPipe pipe)
        {
            return pipe.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return direction != state.get(FACING).getOpposite();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
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
        return MiscUitls.checkType(type, NMBlockEntities.MERGE_ITEM_PIPE, PneumaticPipeBlockEntity::serverTick, world);
    }
}
