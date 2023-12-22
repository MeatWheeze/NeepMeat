package com.neep.neepmeat.transport.fluid_network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;

public interface PipeFlowComponent
{
    // TODO: The only reason why a ServerWorld is required is to call FluidNode::load. Adding a method for registering first-tick listeners should remove this.
    long insert(int fromDir, int toDir, long maxAmount, ServerWorld world, FluidVariant variant, TransactionContext transaction);
}
