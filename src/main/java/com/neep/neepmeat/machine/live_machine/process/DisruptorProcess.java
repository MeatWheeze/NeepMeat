package com.neep.neepmeat.machine.live_machine.process;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import net.minecraft.text.Text;

import java.util.List;

public class DisruptorProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(LivingMachineComponents.DISRUPTOR_SEGMENT);

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {

    }

    @Override
    public List<ComponentType<?>> getRequired()
    {
        return REQUIRED;
    }

    @Override
    public Text getName()
    {
        return Text.of("Disruptor");
    }
}
