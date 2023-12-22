package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface VascularConduitEntity
{
    BlockApiLookup<VascularConduitEntity, Void> LOOKUP = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "vascular_conduit_entity"),
            VascularConduitEntity.class, Void.class);

    static VascularConduitEntity find(World world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof VascularConduitEntity entity)
            return entity;

        return LOOKUP.find(world, pos, null);
    }

    BloodNetwork getNetwork();

    void setNetwork(BloodNetwork network);

    enum UpdateReason
    {
        ADDED,
        REMOVED,
        CHANGED
    }
}
