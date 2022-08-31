package com.neep.neepmeat.util;

import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.transport.machine.item.ItemPumpBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class MiscUtils
{
    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
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
}
