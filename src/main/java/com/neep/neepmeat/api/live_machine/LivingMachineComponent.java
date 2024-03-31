package com.neep.neepmeat.api.live_machine;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public interface LivingMachineComponent
{
    BlockApiLookup<LivingMachineComponent, Void> LOOKUP = BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "living_machine_component"),
            LivingMachineComponent.class, Void.class);

    void setController(BlockPos pos);

    boolean componentRemoved();

    ComponentType<? extends LivingMachineComponent> getComponentType();
}
