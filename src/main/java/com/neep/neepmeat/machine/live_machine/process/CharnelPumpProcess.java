package com.neep.neepmeat.machine.live_machine.process;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpBlockEntity;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.text.Text;

import java.util.List;

public class CharnelPumpProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(
            LivingMachineComponents.CHARNEL_PUMP,
            LivingMachineComponents.MOTOR_PORT,
            LivingMachineComponents.FLUID_INPUT
    );

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.CHARNEL_PUMP, LivingMachineComponents.MOTOR_PORT, LivingMachineComponents.FLUID_INPUT).ifPresent(with ->
        {
            CharnelPumpBlockEntity pump = with.t1().iterator().next();

            Storage<FluidVariant> inputStorage = be.getCombinedFluidInput();
            pump.serverTick(inputStorage);
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
        return Text.of("Charnel Pump");
    }
}
