package com.neep.neepmeat.neepbus;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NeepBusConfigImpl implements NeepBusConfig
{
    @Nullable private Map<String, NeepBusPort> portMap = null;

    private final List<InputEntry> inputs;
    private final List<OutputEntry> outputs;
    private final List<NeepBusPort> inputPorts;

    private final Runnable onInputsChanged;
    private final Runnable onOutputChanged;
    private final Runnable markDirty;

    public NeepBusConfigImpl(List<Entry> inputs, List<Entry> outputs, List<NeepBusPort> inputPorts,
                             Runnable onInputsChanged, Runnable onOutputsChanged, Runnable markDirty)
    {
        this.inputs = inputs.stream().map(InputEntry::new).toList();
        this.outputs = outputs.stream().map(OutputEntry::new).toList();
        this.inputPorts = inputPorts;
        this.onInputsChanged = onInputsChanged;
        this.onOutputChanged = onOutputsChanged;
        this.markDirty = markDirty;
    }

    public static NeepBusConfigImpl ofInputs(List<Entry> inputs, List<NeepBusPort> inputPorts, Runnable onInputsChanged, Runnable markDirty)
    {
        return new NeepBusConfigImpl(inputs, List.of(), inputPorts, onInputsChanged, () -> {}, markDirty);
    }

    public static NeepBusConfigImpl ofOutputs(List<Entry> outputs, Runnable onOutputChanged, Runnable markDirty)
    {
        return new NeepBusConfigImpl(List.of(), outputs, List.of(), () -> {}, onOutputChanged, markDirty);
    }

    @Override
    public List<? extends Entry> getInputs()
    {
        return inputs;
    }

    @Override
    public List<? extends Entry> getOutputs()
    {
        return outputs;
    }

    @Override
    public Map<String, NeepBusPort> getInputPorts()
    {
        if (portMap == null)
        {
            portMap = IntStream.range(0, inputPorts.size())
                    .boxed()
                    .collect(Collectors.<Integer, String, NeepBusPort>toMap(i -> inputs.get(i).getAddress(), inputPorts::get));
        }

        return portMap;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList inputs = new NbtList();
        for (var input : this.inputs)
        {
            NbtCompound entry = new NbtCompound();
            entry.putString("address", input.getAddress());

            inputs.add(entry);
        }
        nbt.put("inputs", inputs);

        NbtList outputs = new NbtList();
        for (var output : this.outputs)
        {
            NbtCompound entry = new NbtCompound();
            entry.putString("address", output.getAddress());

            outputs.add(entry);
        }
        nbt.put("outputs", outputs);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        NbtList inputs = nbt.getList("inputs", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < Math.min(this.inputs.size(), inputs.size()); i++)
        {
            NbtCompound entry = inputs.getCompound(i);

            // Directly set the entry's address to avoid updating the other stuff.
            this.inputs.get(i).entry.setAddress(entry.getString("address"));
        }

        NbtList outputs = nbt.getList("outputs", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < Math.min(this.outputs.size(), outputs.size()); i++)
        {
            NbtCompound entry = outputs.getCompound(i);

            // Directly set the entry's address to avoid updating the other stuff.
            this.outputs.get(i).entry.setAddress(entry.getString("address"));
        }
    }

    private class InputEntry implements Entry
    {
        private final Entry entry;

        private InputEntry(Entry entry)
        {
            this.entry = entry;
        }

        @Override
        public String getName()
        {
            return entry.getName();
        }

        @Override
        public String getAddress()
        {
            return entry.getAddress();
        }

        @Override
        public void setAddress(String address)
        {
            entry.setAddress(address);
            portMap = null;
            onInputsChanged.run();
            markDirty.run();
        }
    }

    private class OutputEntry implements Entry
    {
        private final Entry entry;

        private OutputEntry(Entry entry)
        {
            this.entry = entry;
        }

        @Override
        public String getName()
        {
            return entry.getName();
        }

        @Override
        public String getAddress()
        {
            return entry.getAddress();
        }

        @Override
        public void setAddress(String address)
        {
            entry.setAddress(address);
            onOutputChanged.run();
            markDirty.run();
        }
    }
}
