package com.neep.neepmeat.block.multiblock;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.server.world.ServerWorld;

public interface IControllerBlockEntity
{
    <V extends TransferVariant<?>> Storage<V> getStorage(Class<V> variant);

    void componentBroken(ServerWorld world);
}
