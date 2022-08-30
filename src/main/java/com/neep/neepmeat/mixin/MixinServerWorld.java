package com.neep.neepmeat.mixin;

import com.neep.neepmeat.transport.api.pipe.item_network.ItemNetwork;
import com.neep.neepmeat.transport.data.FluidNetworkManager;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements IServerWorld
{
    @Shadow public abstract void removePlayer(ServerPlayerEntity player, Entity.RemovalReason reason);

    public FluidNodeManager nodeManager = new FluidNodeManager((ServerWorld) (Object) this);
    public ItemNetwork itemNetwork = new ItemNetwork((ServerWorld) (Object) this);
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

    @Override
    public ItemNetwork getItemNetwork()
    {
        return itemNetwork;
    }
}
