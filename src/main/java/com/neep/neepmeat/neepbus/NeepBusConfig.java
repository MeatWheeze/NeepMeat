package com.neep.neepmeat.neepbus;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.Map;

public interface NeepBusConfig extends NbtSerialisable
{
    Map<String, NeepBusPort> getInputPorts();

    List<? extends Entry> getInputs();
    List<? extends Entry> getOutputs();

    interface Entry
    {
        String getName();
        String getAddress();
        void setAddress(String address);
    }

    class SimpleEntry implements Entry
    {
        private final String name;
        private String address;

        public SimpleEntry(String name)
        {
            this.name = name;
            this.address = name.toLowerCase();
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String getAddress()
        {
            return address;
        }

        @Override
        public void setAddress(String address)
        {
            this.address = address;
        }
    }

    NeepBusConfig EMPTY = new NeepBusConfig()
    {
        @Override
        public Map<String, NeepBusPort> getInputPorts()
        {
            return Map.of();
        }

        @Override
        public List<? extends Entry> getInputs()
        {
            return List.of();
        }

        @Override
        public List<? extends Entry> getOutputs()
        {
            return List.of();
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
