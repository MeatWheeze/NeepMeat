package com.neep.neepmeat.api.live_machine;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.text.Text;

import java.util.List;

public interface Process
{
    Text NO_PROCESS = NeepMeat.translationKey("screen", "living_machine.no_process");

    void serverTick(LivingMachineBlockEntity be);

    List<ComponentType<?>> getRequired();

    Text getName();
}
