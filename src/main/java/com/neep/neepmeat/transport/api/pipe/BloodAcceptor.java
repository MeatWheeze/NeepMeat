package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.blood_network.BloodNetwork;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface BloodAcceptor
{
    BlockApiLookup<BloodAcceptor, Direction> SIDED = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "sided_blood_acceptor"),
            BloodAcceptor.class, Direction.class);


    // TODO: rename to consumeOutput
    default long getOutput()
    {
        return 0;
    }
    default float updateInflux(float influx) { return influx; };

    default void setNetwork(@Nullable BloodNetwork network) {};

    Mode getMode();

    enum Mode
    {
        SINK,
        SOURCE,
        ACTIVE_SINK;

        public boolean isOut()
        {
            return this == SOURCE;
        }
    }
}
