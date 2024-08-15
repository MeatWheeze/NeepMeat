package com.neep.neepbus.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepbus.util.NeepBusConfig;
import com.neep.neepbus.util.AbstractInputPort;
import com.neep.neepbus.util.SimpleEntry;
import com.neep.neepbus.part.Indicator;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class VerticalGaugeBlockEntity extends SyncableBlockEntity implements Indicator, GaugeBlockEntity, ConfigProvider
{
    private int value;
    private int minValue = 0;
    private int maxValue = 500;

    private final AbstractInputPort inputPort = new AbstractInputPort()
    {
        @Override
        public void receive(int data)
        {
            if (value != data)
            {
                value = data;
                sync();
            }
        }
    };

    private final NeepBusConfig config = NeepBusConfig.builder(this::sync)
            .input(new SimpleEntry("Input"), inputPort)
            .applyChanges(this)
            .build();

    public VerticalGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        toClientTag(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fromClientTag(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        this.value = nbt.getInt("value");
        this.minValue = nbt.getInt("min_value");
        this.maxValue = nbt.getInt("max_value");
        this.config.readNbt(nbt.getCompound("config"));
    }

    @Override
    public void toClientTag(NbtCompound nbt)
    {
        nbt.putInt("value", value);
        nbt.putInt("min_value", minValue);
        nbt.putInt("max_value", maxValue);
        nbt.put("config", config.writeNbt(new NbtCompound()));
    }

    public NeepBusConfig getConfig()
    {
        return config;
    }

    @Override
    public @Nullable Text getName()
    {
        return null;
    }

    @Override
    public int getValue() { return value; }

    @Override
    public void setValue(int value)
    {
        this.value = value;
        sync();
    }

    @Override
    public int getMinValue() { return minValue; }

    @Override
    public void setMinValue(int minValue)
    {
        this.minValue = minValue;
        sync();
    }

    @Override
    public int getMaxValue() { return maxValue; }

    @Override
    public void setMaxValue(int maxValue)
    {
        this.maxValue = maxValue;
        sync();
    }
}
