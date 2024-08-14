package com.neep.neepmeat.machine.reactor.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.machine.reactor.ReceiverOrganismComponent;
import com.neep.neepbus.util.CachingSender;
import com.neep.neepbus.util.NeepBusConfig;
import com.neep.neepbus.util.SimpleEntry;
import com.neep.neepbus.util.SimpleOutputPort;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class CoreSensorBlockEntity extends SyncableBlockEntity implements ReceiverOrganismComponent
{
    private final CachingSender sender = new CachingSender(this::getWorld, getPos());

    public final SimpleOutputPort organisation = new SimpleOutputPort(new SimpleEntry("Organisation"), sender::send);
    public final SimpleOutputPort incidentZoneRadius = new SimpleOutputPort(new SimpleEntry("Incident zone radius"), sender::send);
    public final SimpleOutputPort exudateFlow = new SimpleOutputPort(new SimpleEntry("Exudate mass flow"), sender::send);
    public final SimpleOutputPort storedExudate = new SimpleOutputPort(new SimpleEntry("Stored exudate"), sender::send);
    public final SimpleOutputPort cudEfficiency = new SimpleOutputPort(new SimpleEntry("Cud efficiency"), sender::send);

    private final NeepBusConfig config = NeepBusConfig.builder(this::markDirty)
            .outputs(organisation, incidentZoneRadius, exudateFlow, storedExudate, cudEfficiency)
            .build();

    public CoreSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public boolean isComponentRemoved()
    {
        return isRemoved();
    }

    public NeepBusConfig getConfig()
    {
        return config;
    }

    public void networkChanged()
    {
        sender.invalidate();
    }
}
