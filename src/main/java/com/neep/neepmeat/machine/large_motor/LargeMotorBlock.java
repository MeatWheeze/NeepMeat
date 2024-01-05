package com.neep.neepmeat.machine.large_motor;

import com.google.common.collect.ImmutableMap;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BlockVolume;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LargeMotorBlock extends BigBlock<LargeMotorStructureBlock> implements MeatlibBlock
{
    private final String registryName;
    public static final BlockVolume VOLUME = BlockVolume.range(
            -1, 0, 0, 1, 2, -1
    );

    public static final VoxelShape NORMAL_SHAPE = VOLUME.toVoxelShape();

//    public final VoxelShape NORTH_SHAPE = NORMAL_SHAPE;
//    public final VoxelShape EAST_SHAPE = rotateShape(NORMAL_SHAPE, 90);
//    public final VoxelShape SOUTH_SHAPE = rotateShape(NORMAL_SHAPE, 180);
//    public final VoxelShape WEST_SHAPE = rotateShape(NORMAL_SHAPE, 270);
//
    private static VoxelShape cuboid(double minX, double minY, double minZ, double sizeX, double sizeY, double sizeZ)
    {
        return VoxelShapes.cuboid(minX, minY, minZ, minX + sizeX, minY + sizeY, minZ + sizeZ);
    }

    private final Map<Direction, VoxelShape> shapeMap = ImmutableMap.of(
        Direction.NORTH, cuboid(-0.5, 0, -1, 2, 2.5, 2),
        Direction.EAST, cuboid(0, 0, -0.5, 2, 2.5, 2),
        Direction.SOUTH, cuboid(-0.5, 0, 0, 2, 2.5, 2),
        Direction.WEST, cuboid(-1, 0, -0.5, 2, 2.5, 2)
    );

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public LargeMotorBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        ItemRegistry.queue(new BaseBlockItem(this, registryName, itemSettings));
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected LargeMotorStructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new LargeMotorStructureBlock(this, FabricBlockSettings.of(Material.METAL)), "large_motor_structure");
    }

    @Override
    protected BlockVolume getVolume(BlockState blockState)
    {
        Direction facing = blockState.get(FACING);
        return VOLUME.rotateY(facing.asRotation() - 180);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
//        return rotateShape(NORMAL_SHAPE, state.get(FACING));
        return shapeMap.get(state.get(FACING));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    public static VoxelShape rotateShape(VoxelShape shape, Direction direction)
    {
//        double s = Math.sin(Math.toRadians(degrees));
//        double c = Math.cos(Math.toRadians(degrees));

        AtomicReference<VoxelShape> newShape = new AtomicReference<>(VoxelShapes.empty());
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
        {
//            double newMinX = (minX * c - minZ * s);
//            double newMaxX = (maxX * c - maxZ * s);
//
//            double newMinZ = (minZ * c + minX * s);
//            double newMaxZ = (maxZ * c + maxX * s);
//
//            if (newMinX > newMaxX)
//            {
//                double temp = newMinX;
//                newMinX = newMaxX;
//                newMaxX = temp;
//            }
//
//            if (newMinZ > newMaxZ)
//            {
//                double temp = newMinZ;
//                newMinZ = newMaxZ;
//                newMaxZ = temp;
//            }

            newShape.set(VoxelShapes.union(newShape.get(), rotatedCuboid(direction, minX, minY, minZ, maxX, maxY, maxZ)));
        });

        return newShape.get().simplify();
    }

    public static VoxelShape rotatedCuboid(Direction direction, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax)
    {
        return switch (direction)
        {
            case NORTH -> VoxelShapes.cuboid(xMin, yMin, zMin, xMax, yMax, zMax);
            case SOUTH -> VoxelShapes.cuboid(xMax, yMin, 1 - zMin, xMin, yMax, 1 - zMax);
            case WEST -> VoxelShapes.cuboid(zMin, yMin, xMin, zMax, yMax, xMax);
            case EAST -> VoxelShapes.cuboid(1 - zMin, yMin, xMin, 1 - zMax, yMax, xMax);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }
}
