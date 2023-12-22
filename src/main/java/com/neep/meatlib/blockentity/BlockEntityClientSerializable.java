package com.neep.meatlib.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public interface BlockEntityClientSerializable
{
    void fromClientTag(NbtCompound nbt);

    NbtCompound toClientTag(NbtCompound nbt);

    default void sync()
    {
        if (this instanceof BlockEntity be)
        {
            be.getWorld().updateListeners(be.getPos(), be.getCachedState(), be.getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }
}
