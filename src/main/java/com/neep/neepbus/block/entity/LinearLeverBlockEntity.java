package com.neep.neepbus.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepbus.part.Slider;
import com.neep.neepbus.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearLeverBlockEntity extends SyncableBlockEntity implements Slider, ConfigProvider
{
    private final CachingSender sender = new CachingSender(this::getWorld, getPos());

    private final DirectReadPort outputPort = new DirectReadPort(
            new SimpleEntry("Output"),
            this::getValue,
            sender::send);

    private final NeepBusConfig config = NeepBusConfig.builder(this::sync)
            .output(outputPort.entry(), outputPort)
            .build();

    private int value = 0;
    private int minValue = 0;
    private int maxValue = 256;
    private int interval = 16;

    public LinearLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public int getValue()
    {
        return value;
    }

    @Override
    public void setValue(int value)
    {
        this.value = MathHelper.clamp(value, minValue, maxValue);
        sync();
    }

    @Override
    public int getMinValue()
    {
        return minValue;
    }

    @Override
    public void setMinValue(int minValue)
    {
        this.minValue = Math.min(minValue, maxValue);
        sync();
    }

    @Override
    public int getMaxValue()
    {
        return maxValue;
    }

    @Override
    public void setMaxValue(int maxValue)
    {
        this.maxValue = Math.max(maxValue, minValue);
        sync();
    }

    @Override
    public int getInterval()
    {
        return interval;
    }

    @Override
    public void setInterval(int interval)
    {
        this.interval = MathHelper.clamp(interval, 1, maxValue - minValue);
        sync();
    }

    public void updateNetwork()
    {
        sender.invalidate();
    }

    @Override
    public NeepBusConfig getConfig()
    {
        return config;
    }

    @Override
    public void increment(double amount, boolean large)
    {
        int increment = (int) (Math.signum(amount) * Math.max(
                (large ? interval : interval / 10f),
                1));
        value = MathHelper.clamp(value + increment, minValue, maxValue);
        outputPort.send();
        sync();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("value", value);
        nbt.putInt("min_value", minValue);
        nbt.putInt("max_value", maxValue);
        nbt.putInt("divisions", interval);

        nbt.put("config", config.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.value = nbt.getInt("value");
        this.minValue = nbt.getInt("min_value");
        this.maxValue = nbt.getInt("max_value");
        this.interval = nbt.getInt("divisions");

        this.config.readNbt(nbt.getCompound("config"));
    }

}
