package com.neep.meatlib.recipe;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage")
public interface MeatlibRecipe<C>
{
    boolean matches(C context);

    boolean takeInputs(C context, TransactionContext transaction);

    boolean ejectOutputs(C context, TransactionContext transaction);

    MeatRecipeType<?> getType();

    MeatRecipeSerialiser<?> getSerialiser();

    Identifier getId();
}
