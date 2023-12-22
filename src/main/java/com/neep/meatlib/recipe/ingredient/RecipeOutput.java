package com.neep.meatlib.recipe.ingredient;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public interface RecipeOutput<T>
{
    T resource();
    long amount();
    long maxAmount();
    long minAmount();
    float chance();
    void update();
    void setNbt(NbtCompound nbt);
    void write(Registry<T> registry, PacketByteBuf buf);
}
