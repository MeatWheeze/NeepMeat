package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.List;
import java.util.stream.StreamSupport;

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

            // Choose a random input if more than one is available.
//            Storage<FluidVariant> input = Iterables.get(fluidInputs, be.getRandom().nextInt(fluidInputs.size()))
//                    .getStorage(null);


            float progressIncrement = be.getProgressIncrement() * 2 / trommels.size();

            try (Transaction transaction = Transaction.openOuter())
            {
                for (var trommel : trommels)
                {
                    var slot = trommel.getStorage();

                    if (slot.isResourceBlank())
                    {
                        // Move fluid from an input slot into the trommel block entity
                        try (Transaction inner = transaction.openNested())
                        {
                            List<StorageView<FluidVariant>> inputViews = fluidInputs.stream()
                                    .map(c -> c.getStorage(null))
                                    .flatMap(s -> StreamSupport.stream(s.nonEmptyViews().spliterator(), false))
                                    .toList();

                            if (!inputViews.isEmpty())
                            {
                                StorageView<FluidVariant> input = inputViews.get(be.getRandom().nextInt(inputViews.size()));
                                // Transfer
                                FluidVariant variant = input.getResource();
                                long extracted = input.extract(variant, 9000, inner);
                                long inserted = slot.insert(variant, extracted, inner);
                                if (inserted == 9000 && slot.getAmount() == 9000)
                                    inner.commit();
                            }
                        }
                    }

                    if (!slot.isResourceBlank())
                    {
                        CombinedStorage<FluidVariant, Storage<FluidVariant>> fluidOutput = new CombinedStorage<>(fluidOutputs.stream().map(c -> c.getStorage(null)).toList());
                        CombinedStorage<ItemVariant, Storage<ItemVariant>> itemOutput = new CombinedStorage<>(itemOutputs.stream().map(c -> c.getStorage(null)).toList());
                        slot.tick(progressIncrement, fluidOutput, itemOutput, transaction);
                    }
                }
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
