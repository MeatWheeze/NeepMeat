package com.neep.neepmeat.recipe.surgery;

import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.neepmeat.machine.surgical_controller.SurgeryTableContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

@Deprecated
public abstract class SurgeryRecipe implements MeatRecipe<SurgeryTableContext>
{
    public abstract boolean isInputEmpty(int recipeProgress);

    public abstract boolean takeInput(SurgeryTableContext context, int recipeProgress, TransactionContext transaction);
}
