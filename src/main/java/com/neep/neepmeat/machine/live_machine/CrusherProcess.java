package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import com.neep.neepmeat.machine.live_machine.block.entity.LuckyOneBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CrusherProcess implements Process
{
    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.ITEM_INPUT, LivingMachineComponents.CRUSHER_SEGMENT, LivingMachineComponents.ITEM_OUTPUT, LivingMachineComponents.MOTOR_PORT).ifPresent(result ->
        {
            var hoppers = result.t1();
            var crushers = result.t2();
            var itemOutputs = result.t3();

            if (crushers.size() > 4)
                return;

            float chanceMod = 0;

            Collection<LuckyOneBlockEntity> luckies = be.getComponent(LivingMachineComponents.LUCKY_ONE);
            for (var lucky : luckies)
            {
                if (chanceMod >= 2)
                    break;

                if (lucky.isActive())
                    chanceMod += 1;
            }

            if (be.getPower() < 0.1)
            {
                return;
            }

//            float progressIncrement = be.getProgressIncrement() / crushers.size() * 2;
            float progressIncrement = be.getProgressIncrement();

            Storage<ItemVariant> input = hoppers.iterator().next().getStorage(null);
            Storage<ItemVariant> output = itemOutputs.iterator().next().getStorage(null);

//            int inputSize = Iterators.size(input.nonEmptyIterator());
            List<StorageView<ItemVariant>> inputViews = new ArrayList<>();
            for (var view : input)
            {
                if (!view.isResourceBlank() && view.getAmount() > 0)
                    inputViews.add(view);
            }

            try (Transaction transaction = Transaction.openOuter())
            {
                for (var crusher : crushers)
                {
                    try (Transaction inner = transaction.openNested())
                    {
                        CrusherSegmentBlockEntity.InputSlot slot = crusher.getStorage();

                        if (inputViews.size() > 0 && slot.isEmpty())
                        {
                            // Round-robin behaviour
                            be.inputSequence = (be.inputSequence + 1) % inputViews.size();

                            try (Transaction inner2 = inner.openNested())
                            {
                                StorageView<ItemVariant> view = inputViews.get(be.inputSequence);
                                if (!view.isResourceBlank() && view.getAmount() > 0)
                                {
                                    long inserted = slot.insert(view.getResource(), view.getAmount(), inner2);
                                    long extracted = view.extract(view.getResource(), inserted, inner2);

                                    if (inserted == extracted)
                                        inner2.commit();
                                    else
                                        inner2.abort();
                                }
                            }
                        }

                        if (!slot.isEmpty())
                        {
                            slot.tick(progressIncrement, output, chanceMod, inner);
                        }
                        inner.commit();
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
                LivingMachineComponents.ITEM_INPUT,
                LivingMachineComponents.CRUSHER_SEGMENT,
                LivingMachineComponents.ITEM_OUTPUT,
                LivingMachineComponents.MOTOR_PORT
        );
    }

    @Override
    public Text getName()
    {
        return Text.of("Crusher");
    }
}
