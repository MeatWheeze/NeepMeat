package com.neep.neepmeat.block.fluid_transport;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Map;

public class PressureGauge extends BaseFacingBlock implements IFluidNodeProvider
{
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.createCuboidShape(3, 0, 0, 13, 15.75, 1),
            Direction.SOUTH, Block.createCuboidShape(3, 0, 15, 13, 15.75, 16),
            Direction.WEST, Block.createCuboidShape(0, 0, 3, 1, 15.75, 13),
            Direction.EAST, Block.createCuboidShape(15, 0, 3, 16, 15.75, 13)));

    public PressureGauge(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayer().isSneaking() ? context.getSide() : context.getSide().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return direction == state.get(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient)
        {
            System.out.println(queryFlow(state, (ServerWorld) world, pos));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean isStorage()
    {
        return false;
    }

    public float queryFlow(BlockState state, ServerWorld world, BlockPos pos)
    {
        Direction facing = state.get(FACING);
        BlockPos pipe = pos.offset(facing);
        FluidNetwork.NodeSupplier supplier = FluidNetwork.getInstance(world).getNodeSupplier(new NodePos(pipe, facing.getOpposite()));
        FluidNode node;
        if ((node = supplier.get()) != null)
        {
            return node.getNetwork().networkPipes.get(pipe).getPressure();
        }
        return 1.33333f;
    }

//    @Override
//    public VoxelShape getShapeForState(BlockState state)
//    {
//        VoxelShape shape = Block.createCuboidShape(4, 4, 4, 12, 12, 12);
//        for (Direction direction : Direction.values())
//        {
//            if (state.get(DIR_TO_CONNECTION.get(direction)))
//            {
//                shape = VoxelShapes.union(shape, DIR_SHAPES.get(direction));
//            }
//        }
//        return shape;
//    }
}
