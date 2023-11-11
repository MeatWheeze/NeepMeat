package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.vat.ItemPortBlock;
import com.neep.neepmeat.transport.blood_network.BloodNetwork;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

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

//    static VascularConduitEntity find(VascularConduitBlockEntity be)
//    {
//    }

    BloodNetwork getNetwork();

    void setNetwork(BloodNetwork network);

    default BlockPos getBlockPos()
    {
        if (this instanceof BlockEntity be)
            return be.getPos();

        throw new NotImplementedException();
    }

//    default void register(World world)
//    {
//        if (world instanceof ServerWorld serverWorld)
//        {
//            BloodNetworkManager.stageEvent(() ->
//                TransportComponents.BLOOD_NETWORK.get(serverWorld.getChunk(getPos()))
//                        .register(this));
//        }
//    }

    enum UpdateReason
    {
        ADDED,
        REMOVED,
        CHANGED
    }
}
