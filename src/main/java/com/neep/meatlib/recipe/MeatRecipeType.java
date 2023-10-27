package com.neep.meatlib.recipe;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import java.util.Optional;

public interface MeatRecipeType<T extends MeatRecipe<?>>
{
    static <T extends MeatRecipe<?>> MeatRecipeType<T> register(final String id)
    {
        return Registry.register(RecipeRegistry.RECIPE_TYPE, new Identifier(id), new MeatRecipeType<T>()
        {

            public String toString()
            {
                return id;
            }
        });
    }

    default <C> Optional<T> match(MeatRecipe<C> recipe, C context)
    {
        return recipe.matches(context) ? Optional.of((T) recipe) : Optional.empty();
    }
}
