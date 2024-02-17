package com.neep.meatlib.recipe;

import net.minecraft.recipe.RecipeType;

import java.util.Optional;

public interface MeatRecipeType<T extends MeatlibRecipe<?>> extends RecipeType<T>
{
//    static <T extends MeatRecipe<?>> MeatRecipeType<T> register(final String id)
//    {
//        return Registry.register(RecipeRegistry.RECIPE_TYPE, new Identifier(id), new MeatRecipeType<T>()
//        {
//
//            public String toString()
//            {
//                return id;
//            }
//        });
//    }

    default <C> Optional<T> match(MeatlibRecipe<C> recipe, C context)
    {
        return recipe.matches(context) ? Optional.of((T) recipe) : Optional.empty();
    }
}
