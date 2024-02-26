package com.neep.neepmeat.machine.integrator;

import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public interface Integrator
{
    static Integrator findIntegrator(World world, BlockPos pos, int maxDist)
    {
        Queue<BlockPos> queue = new LinkedList<>();
        List<BlockPos> visited = new ArrayList<>();
        queue.add(pos);
        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();
            for (Direction direction : Direction.values())
            {
                BlockPos offset = current.offset(direction);

                if (pos.getManhattanDistance(offset) > maxDist || visited.contains(offset)) continue;

                if (world.getBlockState(offset).isOf(NMBlocks.DATA_CABLE))
                {
                    queue.add(offset);
                    visited.add(offset);
                }
                else if (world.getBlockEntity(offset) instanceof Integrator integrator)
                {
                    return integrator;
                }
            }
        }
        return null;
    }

    BlockPos getBlockPos();

    boolean canEnlighten();

    void setLookPos(BlockPos pos);

    void spawnBeam(World world, BlockPos pos);

    static void spawnBeam(ServerWorld world, BlockPos startPos, BlockPos endPos)
    {
        Vec3d start = Vec3d.ofCenter(startPos, 0.9);
        Vec3d end = Vec3d.ofCenter(endPos, 0.8f);
        for (ServerPlayerEntity player : PlayerLookup.around(world, start, 32d))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BEAM, world, start, end, new Vec3d(0, 0, 0), 0.5f, 50);
        }
    }

    long getData(DataVariant variant);

    float extract(DataVariant variant, long amount, TransactionContext transaction);
}
