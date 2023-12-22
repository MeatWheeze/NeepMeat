package com.neep.neepmeat.mixin;

import com.neep.neepmeat.api.enlightenment.EnlightenmentEventManager;
import com.neep.neepmeat.transport.data.PipeNetworkSerialiser;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.item_network.ItemNetworkImpl;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerWorld
{
    @Shadow public abstract void removePlayer(ServerPlayerEntity player, Entity.RemovalReason reason);

    @Unique public FluidNodeManager neepmeat$nodeManager = new FluidNodeManager((ServerWorld) (Object) this);
    @Unique public ItemNetworkImpl neepmeat$itemNetwork = new ItemNetworkImpl((ServerWorld) (Object) this);
    @Unique public EnlightenmentEventManager neepmeat$enlightenmentEventManager = new EnlightenmentEventManager();
    @Unique public PipeNetworkSerialiser neepmeat$networkManager;


    @Override
    public void setFluidNetworkManager(PipeNetworkSerialiser manager)
    {
        this.neepmeat$networkManager = manager;
    }

    @Override
    public FluidNodeManager getFluidNodeManager()
    {
        return neepmeat$nodeManager;
    }

    @Override
    public PipeNetworkSerialiser getPipeNetworkManager()
    {
        return neepmeat$networkManager;
    }

    @Override
    public ItemNetworkImpl getItemNetwork()
    {
        return neepmeat$itemNetwork;
    }

    @Override
    public EnlightenmentEventManager getEnlightenmentEventManager()
    {
        return neepmeat$enlightenmentEventManager;
    }
}
