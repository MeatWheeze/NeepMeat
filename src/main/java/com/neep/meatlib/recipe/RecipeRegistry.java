package com.neep.meatlib.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RecipeRegistry
{
    public static <T extends Recipe<?>> RecipeSerializer<T> registerMeatlibSerializer(final String namespace, final String id, RecipeSerializer<T> serializer)
    {
        return Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(namespace, id), serializer);
    }

    public static <T extends Recipe<?>> RecipeType<T> registerType(final String namespace, final String id)
    {
        return Registry.register(Registries.RECIPE_TYPE, new Identifier(namespace, id), new RecipeType<T>()
        {
            public String toString()
            {
                return id;
            }
        });
    }

    public static <T extends MeatlibRecipe<?>> MeatRecipeType<T> registerMeatlibType(final String namespace, final String id)
    {
        return Registry.register(Registries.RECIPE_TYPE, new Identifier(namespace, id), new MeatRecipeType<T>()
        {
            public String toString()
            {
                return id;
            }
        });
    }

    public static <T extends MeatlibRecipe<?>> MeatRecipeSerialiser<T> registerMeatlibSerializer(final String namespace, final String id, MeatRecipeSerialiser<T> serializer)
    {
        return Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(namespace, id), serializer);
    }
}
