package com.neep.neepmeat.block.fluid_transport;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.pipe.IFluidPipe;
import com.neep.neepmeat.blockentity.CheckValveBlockEntity;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.PipeState;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.item.FluidComponentItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.Function;

public class CheckValveBlock extends BaseFacingBlock implements IFluidPipe, IVariableFlowBlock, PipeState.ISpecialPipe
{
    public static final VoxelShape X_SHAPE = Block.createCuboidShape(0, 4, 4, 16, 12, 12);
    public static final VoxelShape Y_SHAPE = Block.createCuboidShape(4, 0, 4, 12, 16, 12);
    public static final VoxelShape Z_SHAPE = Block.createCuboidShape(4, 4, 0, 12, 12, 16);

    public CheckValveBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, FluidComponentItem::new, settings.nonOpaque());
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient())
        {
            Direction facing = state.get(FACING);
            System.out.println(FluidNetwork.getInstance(world).getNodeSupplier(new NodePos(pos.offset(facing), facing.getOpposite())).get());
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction)
    {
        Direction facing = state.get(FACING);
        return direction == facing || direction == facing.getOpposite();
    }

    @Override
    public AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        Direction facing = state.get(FACING);
        if (direction == facing)
        {
            return AcceptorModes.PUSH;
        }
        else if (direction == facing.getOpposite())
        {
            return AcceptorModes.INSERT_ONLY;
        };
        return AcceptorModes.NONE;
    }

//    @Override
//    public boolean isStorage()
//    {
//        return false;
//    }

//    @Nullable
//    @Override
//    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
//    {
//        return new CheckValveBlockEntity(pos, state);
//    }

    @Override
    public float getFlow(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof CheckValveBlockEntity be)
        {
            return be.getApparentFlow();
        }
        return 0;
    }

    @Override
    public Function<Long, Long> get(Direction bias, BlockState state)
    {
        if (bias == state.get(FACING))
            return Function.identity();
        else
            return flow -> 0L;
    }
}
