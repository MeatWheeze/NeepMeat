package com.neep.meatlib.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

// I couldn't live without this interface
public interface BlockEntityClientSerializable
{
    void fromClientTag(NbtCompound nbt);

    NbtCompound toClientTag(NbtCompound nbt);

    default void sync()
    {
        if (this instanceof BlockEntity be)
        {
            be.markDirty();
            if (be.getWorld() instanceof ServerWorld serverWorld)
            {
//            be.getWorld().updateListeners(be.getPos(), be.getCachedState(), be.getCachedState(), Block.NOTIFY_LISTENERS);
                serverWorld.getChunkManager().markForUpdate(be.getPos());
            }
        }
    }

    default NbtCompound createSyncNbt()
    {
        if (this instanceof BlockEntity blockEntity)
        {
            return blockEntity.toInitialChunkDataNbt();
        }
        return new NbtCompound();
    }

    /**
     * Override to apply baked model updates after sync.
     */
    default void onReceiveNbt(NbtCompound nbt) { }

    default void sendUpdatePacket(List<ServerPlayerEntity> players) {}
}
