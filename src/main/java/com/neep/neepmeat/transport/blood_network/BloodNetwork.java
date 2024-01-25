package com.neep.neepmeat.transport.blood_network;

import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BloodNetwork
{
    UUID getUUID();
    void rebuild(BlockPos pos, VascularConduitEntity.UpdateReason reason);
    void tick();
    void updateTransfer(@Nullable BloodAcceptor changed);
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

    boolean isRemoved();

    boolean isDirty();

    void resetDirty();

    void insert(Collection<VascularConduitEntity> pipes);

    void unload(BlockPos pos, VascularConduitEntity part);
}
