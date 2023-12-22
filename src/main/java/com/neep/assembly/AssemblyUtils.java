package com.neep.assembly;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.PalettedContainer;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class AssemblyUtils
{
    public static void assembleBlocks(World world, BlockPos origin)
    {
        // There is most likely a far simpler algorithm involving far fewer lists, but I am rather stuipd.
        List<BlockPos> queue = new ArrayList<>();
        List<BlockPos> next = new ArrayList<>();
        List<BlockPos> visited = new ArrayList<>();
        List<BlockPos> out = new ArrayList<>();

        queue.add(origin);
        visited.add(origin);
        out.add(origin);
        for (int level = 0; level < 10; ++level)
        {
            for (BlockPos pos : queue)
            {
                System.out.println(pos);
                for (Direction dir : Direction.values())
                {
                    BlockPos newPos = pos.offset(dir);
                    if (AssemblyEntity.canAssemble(world.getBlockState(newPos)) && !visited.contains(newPos))
                    {
                        next.add(newPos);
                        out.add(newPos);
                    }
                    visited.add(newPos);
                }
            }
            queue.clear();
            queue.addAll(next);
            next.clear();
        }

        assembleToEntity(world, origin, out);
    }

    public static void disassemble(World world, AssemblyEntity assembly)
    {
        PalettedContainer<BlockState> states = assembly.getPalette();

        BlockPos origin = assembly.getBlockPos();
        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                for (int k = 0; k < 16; ++k)
                {
                    System.out.println(i + ", " + j + ", " + k);
                    BlockState state = states.get(i, j, k);
                    BlockPos pos = new BlockPos(i, j, k).add(origin);
                    if (world.getBlockState(pos).isAir())
                    {
                        world.setBlockState(pos, state);
                    }
                    else
                    {
//                        world.getBlockState(pos).
                    }
                }
            }
        }
        assembly.remove(Entity.RemovalReason.DISCARDED);
    }

    public static void assembleToEntity(World world, BlockPos origin, List<BlockPos> posList)
    {
        if (posList.isEmpty())
            return;

//        BlockPos max = posList.stream().max(((p1, p2) -> p1.))
        int x1 = origin.getX(), y1 = origin.getY(), z1 = origin.getZ(), x2 = origin.getX(), y2 = origin.getY(), z2 = origin.getZ();

        for (BlockPos pos : posList)
        {
            // Expand bounding coordinates
            if (pos.getX() < x1)
                x1 = pos.getX();
            if (pos.getY() < y1)
                y1 = pos.getY();
            if (pos.getZ() < z1)
                z1 = pos.getZ();

            if (pos.getX() > x2)
                x2 = pos.getX();
            if (pos.getY() > y2)
                y2 = pos.getY();
            if (pos.getZ() > z2)
                z2 = pos.getZ();
        }

        Vec3i start = new Vec3i(x1, y1, z1);
        Vec3i boundary = new Vec3i(x1 - x2, y1 - y2, z1 - z2);
        if (boundary.getX() > 15 || boundary.getY() > 15 || boundary.getZ() > 15)
        {
            return;
        }

        AssemblyEntity assembly = new AssemblyEntity(world);

//        PalettedContainer<BlockState> blocks = assembly.getPalette();
        for (BlockPos pos : posList)
        {
            BlockPos pos1 = pos.subtract(start);
            BlockState state = world.getBlockState(pos);
            assembly.blocks.set(pos1.getX(), pos1.getY(), pos1.getZ(), state);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
//            System.out.println("eeeeee");
        }
        assembly.updatePalette();
        NeepMeat.LOGGER.printf(Level.INFO, "Assembly at: %s", start);
        assembly.setPos(start.getX(), start.getY(), start.getZ());
        world.spawnEntity(assembly);
    }
}
