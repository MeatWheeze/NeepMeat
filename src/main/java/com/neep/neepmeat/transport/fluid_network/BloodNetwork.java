package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface BloodNetwork
{
    void rebuild(BlockPos pos, VascularConduitEntity.UpdateReason reason);
    void tick();
    void add(BlockPos pos, VascularConduitEntity newPart);
    void insert(long pos, VascularConduitEntity newPart);
    void remove(BlockPos pos, VascularConduitEntity part);
    void update(BlockPos pos, VascularConduitEntity part);
    void addAcceptor(long pos, BloodAcceptor acceptor);
    void merge(List<BloodNetwork> adjNetworks);
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

}
