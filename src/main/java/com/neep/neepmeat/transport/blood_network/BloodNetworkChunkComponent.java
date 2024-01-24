package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.*;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BloodNetworkChunkComponent implements Component, ServerTickingComponent
{
    private final Chunk chunk;

    private final Set<VascularConduitEntity> pipes = Collections.newSetFromMap(new WeakHashMap<>());
    private final Multimap<UUID, VascularConduitEntity> loadedPipes = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);
    private final Queue<Pair<UUID, BlockPos>> pipesToLoad = Queues.newArrayDeque();

    // Networks are loaded in the tick loop
    public boolean loaded = false;

    public BloodNetworkChunkComponent(Chunk chunk)
    {
        this.chunk = chunk;
    }

    @Override
    public void serverTick()
    {
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbt)
    {
        NbtList list = nbt.getList("pipes", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); ++i)
        {
            NbtCompound compound = list.getCompound(i);
            UUID uuid = compound.getUuid("uuid");
            BlockPos pos = BlockPos.fromLong(compound.getLong("pos"));

            pipesToLoad.add(Pair.of(uuid, pos));
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbt)
    {
        // I don't think ProtoChunks can ever have pipes in them.
        if (chunk instanceof ProtoChunk)
            return;

        // Pipes are added to networks on the first tick, so it is possible that this method will be called before that happens.
        // In this situation, we need to save pipesToLoad instead of loadedPipes.
        NbtList list = new NbtList();
        if (loaded)
        {
            saveLoadedPipes(list);
        }
        else
        {
            savePipesToLoad(list);
        }
        nbt.put("pipes", list);
    }

    void saveLoadedPipes(NbtList list)
    {
        for (var pipe : pipes)
        {
            var network = pipe.getNetwork();

            // There is no way of distinguishing between unloading and removal of BlockEntities.
            // Dead BEs will stay in the set but will not be saved.
//            if (chunk.getBlockEntityPositions().contains(pipe.getBlockPos()) && network != null)

            if (network == null)
            {
                NeepMeat.LOGGER.warn("Vascular Conduit at " + pipe.getBlockPos() + "has a null network. This indicates that saving or loading has failed.");
                continue;
            }

            UUID uuid = network.getUUID();;
            NbtCompound entry = new NbtCompound();
            entry.putLong("pos", pipe.getBlockPos().asLong());
            entry.putUuid("uuid", uuid);
            list.add(entry);
        }
    }

    void savePipesToLoad(NbtList list)
    {
        for (var pair : pipesToLoad)
        {
            NbtCompound compound = new NbtCompound();
            compound.putUuid("uuid", pair.first());
            compound.putLong("pos", pair.second().asLong());
            list.add(compound);
        }

    }

    @Override
    public boolean equals(Object o)
    {
        return false;
    }

    public Multimap<UUID, VascularConduitEntity> getPipes()
    {
        if (!pipesToLoad.isEmpty())
        {
            pipes.clear();

            while (!pipesToLoad.isEmpty())
            {
                var pair = pipesToLoad.poll();
                var entity = (VascularConduitEntity) chunk.getBlockEntity(pair.second());

                // Invalid NBT or saving weirdness can cause this.
                if (entity == null)
                    continue;

                loadedPipes.put(pair.first(), entity);
                pipes.add(entity);
            }

            chunk.setNeedsSaving(true);
        }
        return loadedPipes;
    }

    public void register(VascularConduitEntity vascularConduitEntity)
    {
        pipes.add(vascularConduitEntity);
        chunk.setNeedsSaving(true);
    }

    public void unregister(VascularConduitEntity vascularConduitEntity)
    {
        pipes.remove(vascularConduitEntity);
        chunk.setNeedsSaving(true);
    }
}
