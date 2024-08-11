package com.neep.neepmeat.neepbus;

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

    public NeepBusConfigImpl(List<Entry> inputs, List<Entry> outputs, List<NeepBusPort> inputPorts,
                             Runnable onInputsChanged, Runnable onOutputsChanged)
    {
        this.inputs = inputs.stream().map(InputEntry::new).toList();
        this.outputs = outputs.stream().map(OutputEntry::new).toList();
        this.inputPorts = inputPorts;
        this.onInputsChanged = onInputsChanged;
        this.onOutputChanged = onOutputsChanged;
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
        }
    }
}
