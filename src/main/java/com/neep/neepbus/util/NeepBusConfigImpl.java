package com.neep.neepbus.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NeepBusConfigImpl implements NeepBusConfig
{
    @Nullable private Map<String, NeepBusPort> portMap = null;

    private final List<InputEntry> inputs;
    private final List<OutputEntry> outputs;
    private final List<AbstractInputPort> inputPorts;

    private final Runnable onOutputChanged;
    private final Runnable markDirty;
    private final Runnable applyChanges;

    public NeepBusConfigImpl(List<ConfigEntry> inputs, List<ConfigEntry> outputs, List<AbstractInputPort> inputPorts,
                             Runnable onOutputsChanged, Runnable markDirty, Runnable applyChanges)
    {
        this.inputs = inputs.stream().map(InputEntry::new).toList();
        this.outputs = outputs.stream().map(OutputEntry::new).toList();
        this.inputPorts = inputPorts;
        this.onOutputChanged = onOutputsChanged;
        this.markDirty = markDirty;
        this.applyChanges = applyChanges;
    }

    @Override
    public List<? extends ConfigEntry> getInputs()
    {
        return inputs;
    }

    @Override
    public List<? extends ConfigEntry> getOutputs()
    {
        return outputs;
    }

    @Override
    public void applyChanges()
    {
        applyChanges.run();
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

    private void onInputsChanged()
    {
        inputPorts.forEach(AbstractInputPort::invalidateAddress);
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

    private class InputEntry implements ConfigEntry
    {
        private final ConfigEntry entry;

        private InputEntry(ConfigEntry entry)
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
            onInputsChanged();
            markDirty.run();
        }
    }

    private class OutputEntry implements ConfigEntry
    {
        private final ConfigEntry entry;

        private OutputEntry(ConfigEntry entry)
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

    public static class Builder
    {
        protected final Runnable markDirty;
        protected List<ConfigEntry> inputs = new ObjectArrayList<>();
        protected List<ConfigEntry> outputs = new ObjectArrayList<>();
        protected List<AbstractInputPort> inputPorts = new ObjectArrayList<>();
        protected Runnable onOutputsChanged = () -> {};
        protected Runnable applyChanges = () -> {};

        public Builder(Runnable markDirty)
        {
            this.markDirty = markDirty;
        }

        public NeepBusConfigImpl build()
        {
            return new NeepBusConfigImpl(inputs, outputs, inputPorts, onOutputsChanged, markDirty, applyChanges);
        }

        public Builder inputs(List<ConfigEntry> inputs, List<AbstractInputPort> inputPorts)
        {
            this.inputs = inputs;
            this.inputPorts = inputPorts;
            return this;
        }

        public Builder input(ConfigEntry input, AbstractInputPort inputPort)
        {
            this.inputs.add(input);
            this.inputPorts.add(inputPort);
            return this;
        }

        public Builder outputs(List<ConfigEntry> outputs)
        {
            this.outputs = outputs;
            return this;
        }

        public Builder outputs(SimpleOutputPort... ports)
        {
            Arrays.stream(ports).map(SimpleOutputPort::entry).forEach(outputs::add);
            return this;
        }

        public Builder output(ConfigEntry output)
        {
            this.outputs.add(output);
            return this;
        }

        public Builder onOutputsChanged(Runnable changed)
        {
            this.onOutputsChanged = changed;
            return this;
        }

        public Builder applyChanges(BlockEntity be)
        {
            applyChanges = () -> NeepBusUtil.floodUpdate(be.getWorld(), be.getPos());
            return this;
        }
    }
}
