package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Map;

public class SinkBlock extends BaseHorFacingBlock
{
    public static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, makeShape(Direction.NORTH),
            Direction.EAST, makeShape(Direction.EAST),
            Direction.SOUTH, makeShape(Direction.SOUTH),
            Direction.WEST, makeShape(Direction.WEST)
        );

    public SinkBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    // Auto-generated
    public static VoxelShape makeShape(Direction facing)
    {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, -0.125, 0.8125, 0.875, 0.1875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.1875, 0.9375, 0.875, 0.375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.28125, 0.6875, 0.5625, 0.34375, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.15625, 0.875, 0.5625, 0.28125, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.375, 0.9375, 0.8125, 0.4375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.375, 0.8125, 0.1875, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0, 0.4375, 0.8125, 0.1875, 0.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.4375, 0.25, 0.1875, 0.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, -0.0625, 0.4375, 0.1875, 0.1875, 0.8125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, -0.125, 0.4375, 0.8125, 0, 0.8125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, -0.0625, 0.4375, 0.875, 0.1875, 0.8125));

        final VoxelShape[] transformed = {VoxelShapes.empty()};

        double angle = getFacingAngle(facing);
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
        {
            Vec3d min = new Vec3d(minX, minY, minZ)
                    .add(-0.5, -0.5, -0.5)
                    .rotateY((float) angle)
                    .add(0.5, 0.5, 0.5);
            Vec3d max = new Vec3d(maxX, maxY, maxZ)
                    .add(-0.5, -0.5, -0.5)
                    .rotateY((float) angle)
                    .add(0.5, 0.5, 0.5);

            VoxelShape next = VoxelShapes.cuboid(new Box(min, max));
            transformed[0] = VoxelShapes.union(transformed[0], next);
        });

        transformed[0].simplify();
        return transformed[0];
    }

    protected static double getFacingAngle(Direction facing)
    {
        return switch (facing)
        {
            case NORTH -> 0;
            case EAST -> -Math.PI / 2;
            case SOUTH -> Math.PI;
            case WEST -> Math.PI / 2;
            default -> 0;
        };
    }
}