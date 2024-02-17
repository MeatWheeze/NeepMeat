package com.neep.neepmeat.compat.kubejs;

import com.neep.meatlib.recipe.MeatRecipeManager;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import net.minecraft.recipe.Recipe;
import org.jetbrains.annotations.Nullable;

public class NMKubeJSPlugin extends KubeJSPlugin
{
//    static final RecipeSchema ARGH = new RecipeSchema(MeatlibRecipeJS.class, MeatlibRecipeJS::new, )

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event)
    {
        for (var recipe : MeatRecipeManager.getInstance().values())
        {
        }
    }

    @Override
    public void registerEvents()
    {
    }

    public static class MeatlibRecipeJS extends RecipeJS
    {
        public MeatlibRecipeJS()
        {

        }

        @Override
        public @Nullable Recipe<?> createRecipe()
        {
            return null;
        }
    }
}
