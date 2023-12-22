package com.neep.neepmeat.recipe;

import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.neepmeat.machine.casting_basin.CastingBasinStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public abstract class AbstractPressingRecipe<T extends CastingBasinStorage> extends ImplementedRecipe<T>
{
    public abstract FluidVariant takeInputs(T storage, TransactionContext transaction);
}
