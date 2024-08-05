package com.neep.neepmeat.machine.live_machine.process;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.text.Text;

import java.util.List;

public class BioreactorProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(
            LivingMachineComponents.STOMACH,
            LivingMachineComponents.FLUID_INPUT,
            LivingMachineComponents.FLUID_OUTPUT
    );

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.STOMACH, LivingMachineComponents.FLUID_INPUT, LivingMachineComponents.FLUID_OUTPUT).ifPresent(r ->
        {
            float progressIncrement = be.getProgressIncrement();

            var stomachs = r.t1();
            var stomach = stomachs.iterator().next();

            stomach.getSlot().tickRecipe(progressIncrement, new Context(be));
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
        return Text.of("Bioreactor");
    }

    public record Context(LivingMachineBlockEntity be)
    {
        public Storage<ItemVariant> itemInput()
        {
            return be.getCombinedItemInput();
        }

        public Storage<FluidVariant> fluidInput()
        {
            return be.getCombinedFluidInput();
        }

        public Storage<FluidVariant> fluidOutput()
        {
            return be.getCombinedFluidOutput();
        }
    }
}
