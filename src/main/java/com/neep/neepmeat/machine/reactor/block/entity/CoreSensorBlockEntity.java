package com.neep.neepmeat.machine.reactor.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepbus.block.entity.ConfigProvider;
import com.neep.neepbus.util.*;
import com.neep.neepmeat.machine.reactor.ReceiverOrganismComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CoreSensorBlockEntity extends SyncableBlockEntity implements ReceiverOrganismComponent, ConfigProvider
{
//    private final CachingSender sender = new CachingSender(this::getWorld, getPos());
    private final MultiCachingSender sender = new MultiCachingSender(this::getWorld, getPos(), s -> this.config.hasOutput(s));

    public final SimpleReadPort organisation = new SimpleReadPort(new SimpleEntry("Organisation"), sender::send);
    public final SimpleReadPort incidentZoneRadius = new SimpleReadPort(new SimpleEntry("Incident zone radius"), sender::send);
    public final SimpleReadPort exudateFlow = new SimpleReadPort(new SimpleEntry("Exudate mass flow"), sender::send);
    public final SimpleReadPort storedExudate = new SimpleReadPort(new SimpleEntry("Stored exudate"), sender::send);
    public final SimpleReadPort cudEfficiency = new SimpleReadPort(new SimpleEntry("Cud efficiency"), sender::send);

    private final NeepBusConfigImpl config = NeepBusConfig.builder(this::markDirty)
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

    @Override
    public NeepBusConfig getConfig()
    {
        return config;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        nbt.put("organisation", organisation.writeNbt(new NbtCompound()));
        nbt.put("radius", incidentZoneRadius.writeNbt(new NbtCompound()));
        nbt.put("exudate_flow", exudateFlow.writeNbt(new NbtCompound()));
        nbt.put("stored_exudate", storedExudate.writeNbt(new NbtCompound()));
        nbt.put("efficiency", cudEfficiency.writeNbt(new NbtCompound()));
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        organisation.readNbt(nbt.getCompound("organisation"));
        incidentZoneRadius.readNbt(nbt.getCompound("radius"));
        exudateFlow.readNbt(nbt.getCompound("exudate_flow"));
        storedExudate.readNbt(nbt.getCompound("stored_exudate"));
        cudEfficiency.readNbt(nbt.getCompound("efficiency"));
    }

    public void networkChanged()
    {
        sender.invalidate();
    }
}
