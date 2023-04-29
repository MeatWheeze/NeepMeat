package com.neep.meatlib.recipe;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class RecipeBehaviour<T extends ImplementedRecipe<? extends ImplementedRecipe.DummyInventory>> implements NbtSerialisable
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

    public void load(World world)
    {
        if (currentRecipe == null && recipeId != null)
        {
            currentRecipe = (T) world.getRecipeManager().get(recipeId).orElse(null);
        }
        recipeId = null;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        if (currentRecipe != null)
        {
            tag.putString("recipe", currentRecipe.getId().toString());
        }
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        String id = tag.getString("recipe");
        if (id != null)
        {
            this.recipeId = new Identifier(id);
        }
        else this.recipeId = null;
    }
}
