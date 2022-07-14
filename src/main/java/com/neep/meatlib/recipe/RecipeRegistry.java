package com.neep.meatlib.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RecipeRegistry
{
    public static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(final String namespace, final String id, RecipeSerializer<T> serializer)
    {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(namespace, id), serializer);
    }

    public static <T extends Recipe<?>> RecipeType<T> registerType(final String namespace, final String id)
    {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(id), new RecipeType<T>()
        {
            public String toString()
            {
                return id;
            }
        });
    }
}
