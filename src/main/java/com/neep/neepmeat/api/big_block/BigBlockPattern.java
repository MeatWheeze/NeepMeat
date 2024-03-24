package com.neep.neepmeat.api.big_block;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BigBlockPattern
{
    protected Map<Vec3i, BlockStateProvider> stateProviderMap = Maps.newHashMap();
//    @Nullable private Map<Vec3i, BlockState> stateMap = Maps.newHashMap();

//    public Map<Vec3i, BlockState> stateMap()
//    {
//        if (stateMap == null)
//        {
//            stateMap = new HashMap<>();
//            stateProviderMap.forEach((p, sp) -> stateMap.put(p, sp.get()));
//        }
//        return stateMap;
//    }

    protected Multimap<Vec3i, BlockApiLookup<?, ?>> apiMap = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);
    
    public <T extends BigBlockPattern> T oddCylinder(int radius, int startHeight, int endHeight, BlockStateProvider state)
    {
        return oddCylinder(Vec3i.ZERO, radius, startHeight, endHeight, state);
    }

    public <T extends BigBlockPattern> T oddCylinder(Vec3i origin, int radius, int startHeight, int endHeight, BlockStateProvider state)
    {
        for (int i = -radius; i <= radius; ++i)
        {
            for (int j = -radius; j <= radius; ++j)
            {
                for (int k = startHeight; k <= endHeight; ++k)
                {
                    set(origin.getX() + i, origin.getY() + k, origin.getZ() + j, state);
                }
            }
        }
        return (T) this;
    }

    public <T extends BigBlockPattern> T range(int startX, int startY, int startZ, int endX, int endY, int endZ, BlockStateProvider state)
    {
        BlockPos.iterate(new BlockPos(startX, startY, startZ), new BlockPos(endX, endY, endZ)).forEach(mutable ->
        {
            set(mutable.toImmutable(), state);
        });
        return (T) this;
    }

    public static BigBlockPattern makeOddCylinder(int radius, int startHeight, int endHeight, BlockState state)
    {
        return makeOddCylinder(Vec3i.ZERO, radius, startHeight, endHeight, state);
    }

    public static BigBlockPattern makeOddCylinder(Vec3i origin, int radius, int startHeight, int endHeight, BlockState state)
    {
        return new BigBlockPattern().oddCylinder(origin, radius, startHeight, endHeight, () -> state);
    }

    public static BigBlockPattern makeRange(int startX, int startY, int startZ, int endX, int endY, int endZ, BlockState state)
    {
        return new BigBlockPattern().range(startX, startY, startZ, endX, endY, endZ, () -> state);
    }

    public BigBlockPattern set(int x, int y, int z, BlockState state)
    {
        return set(new Vec3i(x, y, z), () -> state);
    }

    public BigBlockPattern set(int x, int y, int z, BlockStateProvider state)
    {
        return set(new Vec3i(x, y, z), state);
    }

    public BigBlockPattern set(Vec3i pos, BlockStateProvider state)
    {
        stateProviderMap.put(pos, state);
        return this;
    }

    /**
     * The block entity at the specified location will be flagged with the given API lookup's ID.
     * The structure block entity's API provider still needs to be registered elsewhere.
     */
    public <T, C> BigBlockPattern enableApi(int x, int y, int z, BlockApiLookup<T, C> lookup)
    {
        apiMap.put(new Vec3i(x, y, z), lookup);
        return this;
    }

    public BigBlockPattern remove(Vec3i pos)
    {
        stateProviderMap.remove(pos);
        return this;
    }

    public Iterable<Vec3i> iterable()
    {
        return stateProviderMap.keySet();
    }

    public Iterable<Pair<Vec3i, BlockState>> entries()
    {
        // I don't think this is reusable. Oh, well.
        return stateProviderMap.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue().get()))::iterator;
    }

    public void placeBlocks(World world, BlockPos origin, BlockPos controller)
    {
        BlockPos.Mutable mutable = origin.mutableCopy();
        stateProviderMap.forEach((offset, provider) ->
        {
            mutable.set(origin, offset);

            // Do not replace the controller
            if (mutable.equals(controller))
                return;

            world.setBlockState(mutable, provider.get(), Block.NOTIFY_LISTENERS);
            postProcessBlock(world, offset, mutable, controller, provider.get());
        });
    }

    protected void postProcessBlock(World world, Vec3i offset, BlockPos pos, BlockPos controller, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof BigBlockStructureEntity be)
        {
            be.setController(controller);
            apiMap.get(offset).forEach(be::enableApi);
            world.updateNeighbors(pos, state.getBlock());
        }
    }

    public BigBlockPattern rotateY(float degrees)
    {
        BigBlockPattern newVolume = new BigBlockPattern();

        double s = Math.round(Math.sin(Math.toRadians(degrees)));
        double c = Math.round(Math.cos(Math.toRadians(degrees)));

        stateProviderMap.forEach((offset, state) ->
        {
            int newX = (int) Math.round(offset.getX() * c - offset.getZ() * s);
            int newZ = (int) Math.round(offset.getX() * s + offset.getZ() * c);
            newVolume.set( newX, offset.getY(), newZ, state);
        });

        apiMap.forEach((offset, api) ->
        {
            int newX = (int) Math.round(offset.getX() * c - offset.getZ() * s);
            int newZ = (int) Math.round(offset.getX() * s + offset.getZ() * c);
            newVolume.enableApi(newX, offset.getY(), newZ, api);
        });

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

    @FunctionalInterface
    public interface BlockStateProvider
    {
        BlockState get();
    }
}
