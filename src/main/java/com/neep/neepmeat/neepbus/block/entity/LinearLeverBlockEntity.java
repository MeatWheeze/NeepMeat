package com.neep.neepmeat.neepbus.block.entity;

import com.google.common.base.Suppliers;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.neepbus.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LinearLeverBlockEntity extends SyncableBlockEntity
{
    private int value;
    private final Supplier<CachingSender> sender = Suppliers.memoize(() -> new CachingSender(getWorld(), getPos()));

    private final SimpleOutputPort outputPort = new SimpleOutputPort(new NeepBusConfig.SimpleEntry("Output"),
            (s, value) -> sender.get().send(s, value));

    private final NeepBusConfig config = new NeepBusConfigImpl(
            List.of(),
            List.of(outputPort.entry()),
            List.of(),
            () -> {},
            () -> {}
    );


    public LinearLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public Map<String, NeepBusPort> getPorts()
    {
        return NeepBusProvider.NO_PORTS;
    }

    public void updateNetwork()
    {
        sender.get().clear();
    }

    public NeepBusConfig getConfig()
    {
        return config;
    }

    public void onScroll(double amount)
    {
        value += Math.round(amount);
        outputPort.send(value);
        sync();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("value", value);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.value = nbt.getInt("value");
    }

    public int getValue()
    {
        return value;
    }
}
