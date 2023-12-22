package com.neep.meatlib.recipe.ingredient;

import net.minecraft.nbt.NbtCompound;

public interface RecipeOutput<T>
{
    T resource();
    long amount();
    long maxAmount();
    long minAmount();
    float chance();
    void update();
    void setNbt(NbtCompound nbt);
}
