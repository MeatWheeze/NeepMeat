package com.neep.neepmeat.compat.kubejs;

import com.neep.meatlib.recipe.MeatRecipeManager;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;

public class NMKubeJSPlugin extends KubeJSPlugin
{
    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event)
    {
        for (var recipe : MeatRecipeManager.getInstance().values())
        {
        }
    }

    interface TestSchema
    {
    }

    public static class MeatlibRecipeJS extends RecipeJS
    {
        public MeatlibRecipeJS()
        {

        }
    }
}
