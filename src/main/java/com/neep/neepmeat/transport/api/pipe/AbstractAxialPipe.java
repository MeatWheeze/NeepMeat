package com.neep.neepmeat.transport.api.pipe;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.transport.block.fluid_transport.FluidPipeBlock;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAxialPipe extends BaseFacingBlock implements IFluidPipe
{
    public static final VoxelShape X_SHAPE = Block.createCuboidShape(0, 4, 4, 16, 12, 12);
    public static final VoxelShape Y_SHAPE = Block.createCuboidShape(4, 0, 4, 12, 16, 12);
    public static final VoxelShape Z_SHAPE = Block.createCuboidShape(4, 4, 0, 12, 12, 16);

    public AbstractAxialPipe(String itemName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, factory, settings);
    }


    public VoxelShape getShape(BlockState state)
    {
        Direction facing = state.get(FACING);
        return switch (facing.getAxis())
                {
                    case X -> X_SHAPE;
                    case Y -> Y_SHAPE;
                    case Z -> Z_SHAPE;
                };
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return getShape(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        return getShape(state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.isClient())
            return;

        if (!(world.getBlockState(fromPos).getBlock() instanceof FluidPipeBlock))
        {
            if (createStorageNodes(world, pos, state))
                updateNetwork((ServerWorld) world, pos, PipeNetwork.UpdateReason.NODE_CHANGED);
        }

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        if (world.isClient())
            return;

        createStorageNodes(world, pos, state);
        updateNetwork((ServerWorld) world, pos, PipeNetwork.UpdateReason.PIPE_ADDED);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (world.isClient())
            return;

        if (!state.isOf(newState.getBlock()))
        {
            removePipe((ServerWorld) world, state, pos);
        }
    }

    @Override
    public boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof FacingBlock)
        {
            Direction facing = state.get(FacingBlock.FACING);
            return direction == facing || direction == facing.getOpposite();
        }
        return true;
    }

    public AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return AcceptorModes.INSERT_EXTRACT;
    }
}
