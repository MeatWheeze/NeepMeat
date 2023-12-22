package com.neep.meatlib.recipe;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage")
public interface MeatRecipe<C>
{
    boolean matches(C context, TransactionContext transaction);

    boolean takeInputs(C context, TransactionContext transaction);

    boolean ejectOutputs(C context, TransactionContext transaction);

    MeatRecipeType<?> getType();

    <T extends MeatRecipe<?>> MeatRecipeSerialiser<T> getSerialiser();

    Identifier getId();
}
