package com.neep.meatlib.recipe;

import com.neep.neepmeat.recipe.EnlighteningRecipe;
import net.minecraft.util.Identifier;

public abstract class RecipeBehaviour<T extends ImplementedRecipe<? extends ImplementedRecipe.DummyInventory>>
{
    protected Identifier recipeId;
    protected T currentRecipe;

    public T getCurrentRecipe()
    {
        return currentRecipe;
    }

    public Identifier getRecipeId()
    {
        return recipeId;
    }

    public void setRecipe(T recipe)
    {
        this.currentRecipe = recipe;
        if (recipe == null) recipeId = null;
        else recipeId = recipe.getId();
    }

    public abstract void startRecipe(T recipe);

    public abstract void interrupt();

    public abstract void finishRecipe();
}
