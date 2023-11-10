package com.neep.neepmeat.transport.blood_network;

import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.block.energy_transport.entity.VascularConduitBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BloodNetwork
{
    UUID getUUID();
    NbtCompound toNbt();

    void rebuild(BlockPos pos, VascularConduitEntity.UpdateReason reason);
    void tick();
    void add(BlockPos pos, VascularConduitEntity newPart);
    void remove(BlockPos pos, VascularConduitEntity part);
    void merge(List<BloodNetwork> adjNetworks);

    void update(BlockPos pos, VascularConduitEntity part);

    void mergeInto(BloodNetwork other);

    static BloodNetwork find(World world, BlockPos pos)
    {
        var conduit = VascularConduitEntity.find(world, pos);
        if (conduit != null)
        {
            return conduit.getNetwork();
        }
        return null;
    }

    static void retrieveNetwork(ServerWorld world, UUID uuid)
    {
    }

    boolean isRemoved();

    boolean isDirty();

    void resetDirty();

    void insert(Collection<VascularConduitEntity> pipes);

    void unload(BlockPos pos, VascularConduitBlockEntity part);
}
