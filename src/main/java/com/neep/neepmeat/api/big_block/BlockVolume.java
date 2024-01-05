package com.neep.neepmeat.api.big_block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.HashSet;
import java.util.Set;

public class BlockVolume
{
    protected Set<Vec3i> set = new HashSet<>();

    public static BlockVolume oddCylinder(int radius, int startHeight, int endHeight)
    {
        BlockVolume volume = new BlockVolume();
        for (int i = -radius; i <= radius; ++i)
        {
            for (int j = -radius; j <= radius; ++j)
            {
                for (int k = startHeight; k <= endHeight; ++k)
                {
                    volume.add(new Vec3i(i, k, j));
                }
            }
        }
        return volume;
    }

    public static BlockVolume range(int startX, int startY, int startZ, int endX, int endY, int endZ)
    {
        BlockVolume volume = new BlockVolume();
        BlockPos.iterate(new BlockPos(startX, startY, startZ), new BlockPos(endX, endY, endZ)).forEach(mutable ->
        {
            volume.add(mutable.toImmutable());
        });
        return volume;
    }

    public Iterable<Vec3i> iterable()
    {
        return set;
    }

    public boolean add(Vec3i pos)
    {
        return set.add(pos);
    }

    public boolean remove(Vec3i pos)
    {
        return set.remove(pos);
    }

    public BlockVolume rotateY(float degrees)
    {
        BlockVolume newVolume = new BlockVolume();
        for (Vec3i offset : set)
        {
            double s = Math.sin(Math.toRadians(degrees));
            double c = Math.cos(Math.toRadians(degrees));
            newVolume.add(new Vec3i(
                    Math.round(offset.getX() * c - offset.getZ() * s),
                    offset.getY(),
                    Math.round(offset.getX() * s + offset.getZ() * c)
            ));
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
