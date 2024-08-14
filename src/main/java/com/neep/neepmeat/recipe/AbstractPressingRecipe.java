package com.neep.neepmeat.recipe;

import com.neep.meatlib.recipe.MeatlibRecipe;
import com.neep.neepmeat.machine.casting_basin.CastingBasinStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public abstract class AbstractPressingRecipe<T extends CastingBasinStorage> implements MeatlibRecipe<T>
{
    @Override
    public boolean ejectOutputs(T context, TransactionContext transaction)
    {
        return false;
    }
}
