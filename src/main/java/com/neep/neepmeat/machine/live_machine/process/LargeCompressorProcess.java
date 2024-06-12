package com.neep.neepmeat.machine.live_machine.process;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import net.minecraft.text.Text;

import java.util.List;

public class LargeCompressorProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(
            LivingMachineComponents.MOTOR_PORT,
            LivingMachineComponents.LARGE_TROMMEL
    );

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.MOTOR_PORT, LivingMachineComponents.LARGE_COMPRESSOR).ifPresent(r ->
        {
            var motors = r.t1();
            var compressors = r.t2();


        });
    }

    @Override
    public List<ComponentType<?>> getRequired()
    {
        return REQUIRED;
    }

    @Override
    public Text getName()
    {
        return Text.of("Large Compressor");
    }
}
