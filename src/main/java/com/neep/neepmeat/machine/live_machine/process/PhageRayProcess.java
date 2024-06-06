package com.neep.neepmeat.machine.live_machine.process;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.component.ItemOutputComponent;
import com.neep.neepmeat.machine.phage_ray.PhageRayBlockEntity;
import com.neep.neepmeat.machine.phage_ray.PhageRayEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

public class PhageRayProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(
            LivingMachineComponents.PHAGE_RAY
    );

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.PHAGE_RAY).ifPresent(result ->
        {
            Collection<ItemOutputComponent> itemOutputs = be.getComponent(LivingMachineComponents.ITEM_OUTPUT);

            boolean canHarvest =
                    !itemOutputs.isEmpty() &&
                    !be.getComponent(LivingMachineComponents.EXTRACTOR).isEmpty();

            PhageRayBlockEntity ray = result.t1().iterator().next();
            PhageRayEntity entity = ray.getTetheredEntity();
            try (Transaction transaction = Transaction.openOuter())
            {
                if (entity != null)
                {
                    entity.tickProcess(canHarvest, be.getCombinedItemOutput(), transaction);
                }
                transaction.commit();
            }
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
        return Text.of("Phage Ray");
    }
}
