package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.fluid.Fluids;

import java.util.List;

public class LargeTrommelProcess implements Process
{
    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(
                LivingMachineComponents.LARGE_TROMMEL,
                LivingMachineComponents.FLUID_INPUT,
                LivingMachineComponents.FLUID_OUTPUT,
                LivingMachineComponents.ITEM_OUTPUT,
                LivingMachineComponents.MOTOR_PORT).ifPresent(result ->
        {
            var trommels = result.t1();
            var fluidInputs = result.t2();
            var fluidOutputs = result.t3();
            var itemOutputs = result.t4();

            if (be.getPower() < 0.1)
            {
                return;
            }

            float progressIncrement = be.getProgressIncrement() * 2;

            try (Transaction transaction = Transaction.openOuter())
            {
                long extracted = fluidInputs.iterator().next().getStorage(null).extract(FluidVariant.of(Fluids.WATER), 1000, transaction);
                long inserted = fluidOutputs.iterator().next().getStorage(null).insert(FluidVariant.of(Fluids.WATER), extracted, transaction);
//                StorageUtil.move(fluidInputs.iterator().next().getStorage(null), fluidOutputs.iterator().next().getStorage(null), v -> true,
//                        100, transaction);
                transaction.commit();
            }
        });
    }

    @Override
    public List<ComponentType<?>> getRequired()
    {
        return List.of(
                LivingMachineComponents.LARGE_TROMMEL,
                LivingMachineComponents.FLUID_INPUT,
                LivingMachineComponents.FLUID_OUTPUT,
                LivingMachineComponents.ITEM_OUTPUT,
                LivingMachineComponents.MOTOR_PORT
        );
    }
}
