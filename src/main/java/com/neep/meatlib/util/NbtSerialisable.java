package com.neep.meatlib.util;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerialisable
{
    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);
}
