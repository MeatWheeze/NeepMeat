package com.neep.neepmeat.mixin;

import com.neep.neepmeat.transport.data.FluidNetworkManager;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements IServerWorld
{
    public FluidNodeManager nodeManager = new FluidNodeManager((ServerWorld) (Object) this);
    public FluidNetworkManager networkManager;

    @Override
    public void setFluidNetworkManager(FluidNetworkManager manager)
    {
        this.networkManager = manager;
    }

    @Override
    public FluidNodeManager getFluidNodeManager()
    {
        return nodeManager;
    }

    @Override
    public FluidNetworkManager getFluidNetworkManager()
    {
        return networkManager;
    }
}
