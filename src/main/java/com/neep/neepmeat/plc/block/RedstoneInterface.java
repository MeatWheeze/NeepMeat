package com.neep.neepmeat.plc.block;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface RedstoneInterface
{
    BlockApiLookup<RedstoneInterface, Direction> LOOKUP = BlockApiLookup.get(new Identifier(
            NeepMeat.NAMESPACE, "redstone_interface_sided"),
            RedstoneInterface.class, Direction.class);

    int getReceivedStrength();

    void setEmittedStrength(int strength);
}
