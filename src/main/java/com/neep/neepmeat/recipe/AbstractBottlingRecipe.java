package com.neep.neepmeat.recipe;

import com.neep.meatlib.recipe.MeatlibRecipe;
import com.neep.neepmeat.machine.bottler.BottlerStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;

public abstract class AbstractBottlingRecipe implements MeatlibRecipe<BottlerStorage>
{
    protected final Identifier id;
    protected final int processTime;

    protected AbstractBottlingRecipe(Identifier id, int processTime)
    {
        this.id = id;
        this.processTime = processTime;
    }

    @Override
    public boolean matches(BottlerStorage context)
    {
        return false;
    }

    @Override
    public boolean takeInputs(BottlerStorage context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public boolean ejectOutputs(BottlerStorage context, TransactionContext transaction)
    {
        return false;
    }

    public Identifier getId()
    {
        return id;
    }
}
