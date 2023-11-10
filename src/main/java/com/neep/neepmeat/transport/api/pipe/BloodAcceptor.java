package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface BloodAcceptor
{
    BlockApiLookup<BloodAcceptor, Direction> SIDED = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "sided_blood_acceptor"),
            BloodAcceptor.class, Direction.class);


    default long getOutput()
    {
        return 0;
    }

    default void updateInflux(float influx) {};

    Mode getMode();

    enum Mode
    {
        IN,
        OUT;

        public boolean isOut()
        {
            return this == OUT;
        }
    }
}
