package com.neep.meatlib.util;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerialisable
{
    void writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);
}
