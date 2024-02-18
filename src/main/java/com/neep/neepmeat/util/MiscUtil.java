package com.neep.neepmeat.util;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MiscUtil
{
    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, @Nullable BlockEntityTicker<E> serverTicker, @Nullable BlockEntityTicker<E> clientTicker, World world)
    {
        return expectedType == givenType ? world.isClient ? (BlockEntityTicker<A>) clientTicker : (BlockEntityTicker <A>) serverTicker
                : null;
    }

    public static <T extends Entity> T closestEntity(List<T> entityList, Vec3d pos)
    {
        T out;
        if (entityList.isEmpty())
        {
            return null;
        }
        else
        {
            out = entityList.get(0);
        }
        for (T entity : entityList)
        {
            double distance = pos.squaredDistanceTo(out.getX(), out.getY(), out.getZ());
            if (pos.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) < distance)
            {
                out = entity;
            }
        }
        return out;
    }

    public static long dropletsToMb(long droplets)
    {
        return Math.floorDiv(droplets, FluidConstants.BUCKET / 1000);
    }

    public static VoxelShape rotateShapeY(VoxelShape shape, float angle)
    {
        double s = Math.sin(Math.toRadians(angle));
        double c = Math.cos(Math.toRadians(angle));

        List<VoxelShape> newShapes = Lists.newArrayList();
        shape.forEachBox((x1, y1, z1, x2, y2, z2) ->
        {
            x1 -= 0.5;
            z1 -= 0.5;
            x2 -= 0.5;
            z2 -= 0.5;

            double newX1 = (x1 * c - z1 * s);
            double newZ1 = (x1 * s + z1 * c);
            double newX2 = (x2 * c - z2 * s);
            double newZ2 = (x2 * s + z2 * c);

            newX1 += 0.5;
            newZ1 += 0.5;
            newX2 += 0.5;
            newZ2 += 0.5;

            if (newX1 > newX2)
            {
                var temp = newX1;
                newX1 = newX2;
                newX2 = temp;

            }

            if (newZ1 > newZ2)
            {
                var temp = newZ1;
                newZ1 = newZ2;
                newZ2 = temp;
            }

            newShapes.add(VoxelShapes.cuboid(newX1, y1, newZ1, newX2, y2, newZ2));
        });

        VoxelShape newShape = VoxelShapes.empty();
        for (var shape1 : newShapes)
        {
            newShape = VoxelShapes.combineAndSimplify(newShape, shape1, BooleanBiFunction.OR);
        }
        return newShape;
    }
}
