package com.neep.neepmeat.api;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;

public interface Burner
{
    BlockApiLookup<Burner, Void> LOOKUP =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "burner_lookup"), Burner.class, Void.class);

    default void tickPowerConsumption() {};

    double getOutputPower();
}
