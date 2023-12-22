package com.neep.neepmeat.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MiscUtils
{
    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> serverTicker, BlockEntityTicker<E> clientTicker, World world)
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
}
