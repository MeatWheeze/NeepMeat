package com.neep.neepmeat.api.big_block;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Map;

public class BigBlockPattern
{
    protected Map<Vec3i, BlockState> stateMap = Maps.newHashMap();
    protected Multimap<Vec3i, BlockApiLookup<?, ?>> apiMap = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);

    public static BigBlockPattern oddCylinder(int radius, int startHeight, int endHeight, BlockState state)
    {
        BigBlockPattern volume = new BigBlockPattern();
        for (int i = -radius; i <= radius; ++i)
        {
            for (int j = -radius; j <= radius; ++j)
            {
                for (int k = startHeight; k <= endHeight; ++k)
                {
                    volume.set(i, k, j, state);
                }
            }
        }
        return volume;
    }

    public static BigBlockPattern range(int startX, int startY, int startZ, int endX, int endY, int endZ, BlockState state)
    {
        BigBlockPattern volume = new BigBlockPattern();
        BlockPos.iterate(new BlockPos(startX, startY, startZ), new BlockPos(endX, endY, endZ)).forEach(mutable ->
        {
            volume.set(mutable.toImmutable(), state);
        });
        return volume;
    }

    public BigBlockPattern set(int x, int y, int z, BlockState state)
    {
        return set(new Vec3i(x, y, z), state);
    }

    public BigBlockPattern set(Vec3i pos, BlockState state)
    {
        stateMap.put(pos, state);
        return this;
    }

    /**
     * The block entity at the specified location will be flagged with the given API lookup's ID.
     * The block entity's API provider still needs to be registered elsewhere.
     */
    public <T, C> BigBlockPattern enableApi(int x, int y, int z, BlockApiLookup<T, C> lookup)
    {
        apiMap.put(new Vec3i(x, y, z), lookup);
        return this;
    }

    public BigBlockPattern remove(Vec3i pos)
    {
        stateMap.remove(pos);
        return this;
    }

    public Iterable<Vec3i> iterable()
    {
        return stateMap.keySet();
    }

    public void placeBlocks(World world, BlockPos origin, BlockPos controller)
    {
        BlockPos.Mutable mutable = origin.mutableCopy();
        stateMap.forEach((offset, state) ->
        {
            mutable.set(origin, offset);

            // Do not replace the controller
            if (mutable.equals(controller))
                return;

            world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS);
//            world.setBlockState(mutable, Blocks.STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
            if (world.getBlockEntity(mutable) instanceof BigBlockStructureEntity be)
            {
                be.setController(controller);
                apiMap.get(offset).forEach(be::enableApi);
                world.updateNeighbors(mutable, state.getBlock());
            }
        });
    }

    public BigBlockPattern rotateY(float degrees)
    {
        BigBlockPattern newVolume = new BigBlockPattern();
        for (Map.Entry<Vec3i, BlockState> entry : stateMap.entrySet())
        {
            Vec3i offset = entry.getKey();

            double s = Math.round(Math.sin(Math.toRadians(degrees)));
            double c = Math.round(Math.cos(Math.toRadians(degrees)));
            newVolume.set(
                    (int) Math.round(offset.getX() * c - offset.getZ() * s),
                    offset.getY(),
                    (int) Math.round(offset.getX() * s + offset.getZ() * c),
                    entry.getValue()
            );
        }
        return newVolume;
    }

    public VoxelShape toVoxelShape()
    {
        VoxelShape shape = VoxelShapes.empty();
        for (Vec3i vec : iterable())
        {
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(vec.getX(), vec.getY(), vec.getZ(), vec.getX() + 1, vec.getY() + 1, vec.getZ() + 1));
        }
        return shape.simplify();
    }

    public Box toBox(BlockPos origin)
    {
        Box box = new Box(0, 0, 0, 0, 0, 0);
        for (Vec3i vec : iterable())
        {
            box = box.union(new Box(vec.getX(), vec.getY(), vec.getZ(), vec.getX() + 1, vec.getY() + 1, vec.getZ() + 1));
        }
        return box.offset(origin);
    }
}
