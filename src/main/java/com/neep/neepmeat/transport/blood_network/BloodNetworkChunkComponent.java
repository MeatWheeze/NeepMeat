package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class BloodNetworkChunkComponent implements Component, ServerTickingComponent
{
    private final Chunk chunk;
    private final Set<BloodNetwork> activeNetworks = Sets.newHashSet();
    private final Queue<NbtCompound> stagedNetworks = Queues.newArrayDeque();

    public BloodNetworkChunkComponent(Chunk chunk)
    {
        this.chunk = chunk;
//        this.world = (ServerWorld) ((WorldChunk) chunk).getWorld();
    }

    public void addNetwork(BloodNetwork network)
    {
        activeNetworks.add(network);
    }

    @Override
    public void serverTick()
    {
        while (!stagedNetworks.isEmpty())
        {
            NbtCompound nbt = stagedNetworks.poll();
            activeNetworks.add(BloodNetworkImpl.fromNbt(getWorld(), UUID.randomUUID(), nbt));
        }

        var tickIt = activeNetworks.iterator();
        while (tickIt.hasNext())
        {
            var network = tickIt.next();
            if (network.isRemoved())
            {
                tickIt.remove();
            }
            else
            {
                network.tick();
            }
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbt)
    {
        activeNetworks.clear();
        stagedNetworks.clear();

        NbtList list = nbt.getList("networks", NbtElement.COMPOUND_TYPE);
        for (var element : list)
        {
            if (element instanceof NbtCompound compound)
            {
                stagedNetworks.add(compound);
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbt)
    {
        NbtList list = new NbtList();
        for (var network : activeNetworks)
        {
            list.add(network.toNbt());
        }
        nbt.put("networks", list);
    }

    @Override
    public boolean equals(Object o)
    {
        return false;
    }

    private ServerWorld getWorld()
    {
        if (chunk instanceof WorldChunk worldChunk && worldChunk.getWorld() instanceof ServerWorld serverWorld)
            return serverWorld;

        throw new IllegalStateException("BloodNetworkChunkComponent created on client or on ");
    }

}
