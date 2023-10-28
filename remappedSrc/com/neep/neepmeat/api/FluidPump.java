package com.neep.neepmeat.api;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.lwjgl.system.CallbackI;

import java.util.function.Supplier;

public interface FluidPump
{
    static FluidPump of(float flow, boolean isStorage)
    {
        return new FluidPump()
        {
            @Override
            public float getFlow()
            {
                return getMode().isDriving() ? flow : 0;
            }

            @Override
            public AcceptorModes getMode()
            {
                return AcceptorModes.byFlow(flow);
            }

            @Override
            public boolean isStorage()
            {
                return isStorage;
            }
        };
    }

    static FluidPump of(float flow, Supplier<AcceptorModes> supplier, boolean isStorage)
    {
        return new FluidPump()
        {
            @Override
            public float getFlow()
            {
                return getMode().isDriving() ? flow : 0;
            }

            @Override
            public AcceptorModes getMode()
            {
                return supplier.get();
            }

            @Override
            public boolean isStorage()
            {
                return isStorage;
            }
        };
    }

    float getFlow();
    AcceptorModes getMode();
    boolean isStorage();

    BlockApiLookup<FluidPump, Direction> SIDED =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "sided_fluid_pump"), FluidPump.class, Direction.class);

}
