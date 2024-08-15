package com.neep.neepbus.util;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.Map;

public interface NeepBusConfig extends NbtSerialisable
{
    static NeepBusConfigImpl.Builder builder(Runnable markDirty)
    {
        return new NeepBusConfigImpl.Builder(markDirty);
    }

    Map<String, NeepBusPort> getInputPorts();

    List<? extends ConfigEntry> getInputs();
    List<? extends ConfigEntry> getOutputs();

    /**
     * To be called after input port changes have been made.
     * Particularly relevant when an input address has changed to match an output address elsewhere.
     * Override to emit a flood update through the cable network.
     */
    void applyChanges();

    NeepBusConfig EMPTY = new NeepBusConfig()
    {
        @Override
        public Map<String, NeepBusPort> getInputPorts()
        {
            return Map.of();
        }

        @Override
        public List<? extends ConfigEntry> getInputs()
        {
            return List.of();
        }

        @Override
        public List<? extends ConfigEntry> getOutputs()
        {
            return List.of();
        }

        @Override
        public void applyChanges()
        {

        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt)
        {
            return nbt;
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {

        }
    };
}
