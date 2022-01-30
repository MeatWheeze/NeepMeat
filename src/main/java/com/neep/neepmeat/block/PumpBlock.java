package com.neep.neepmeat.block;

import com.neep.neepmeat.fluid_util.FluidNode;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.blockentity.PumpBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PumpBlock extends BaseFacingBlock implements BlockEntityProvider, DirectionalFluidAcceptor, FluidNodeProvider
{
    public PumpBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new PumpBlockEntity(pos, state);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return checkType(type, BlockEntityInitialiser.PUMP_BLOCK_ENTITY, PumpBlockEntity::tick, world);
    }

    @Override
    public boolean connectInDirection(BlockState state, Direction direction)
    {
        return state.get(FACING).equals(direction) || state.get(FACING).getOpposite().equals(direction);
    }

    @Override
    public AcceptorModes getDirectionMode(BlockState state, Direction direction)
    {
//        return state.get(FACING).equals(direction)
        if (state.get(FACING).equals(direction))
        {
            return AcceptorModes.EXTRACT_ONLY;
        }
        else if (state.get(FACING).equals(direction.getOpposite()))
        {
            return AcceptorModes.INSERT_ONLY;
        }
        return AcceptorModes.NONE;
    }

    @Override
    public FluidNode getNode(World world, BlockPos pos, Direction direction)
    {
        PumpBlockEntity be = (PumpBlockEntity) world.getBlockEntity(pos);
        return be.getNode(direction);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        PumpBlockEntity be = (PumpBlockEntity) world.getBlockEntity(pos);
        if (!world.isClient)
        {
            if (!player.isSneaking())
            {
                be.update(state, world);
            }
            else
            {
//                PumpBlockEntity.tick(world, pos, state, be);
//                be.sides.get(state.get(PumpBlock.FACING)).tick(world);
            }
            player.sendMessage(Text.of(Long.toString(be.getBuffer(null).getAmount())), true);
        }
        return ActionResult.SUCCESS;
    }

//    @Override
//    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
//        Direction facing = state.get(FACING);
//        if (to.getSearchDirection() == facing) {
//            to.offer(EmptyFluidExtractable.SUPPLIER);
//        }
//    }

}
