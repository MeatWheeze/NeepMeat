package com.neep.meatlib.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RecipeRegistry
{
//    public static SimpleRegistry<MeatRecipeType<?>> RECIPE_TYPE = FabricRegistryBuilder.createSimple((Class<MeatRecipeType<?>>) (Class<?>) MeatRecipeType.class,
//            new Identifier(MeatLib.NAMESPACE, "recipe_type")).buildAndRegister();

//    public static SimpleRegistry<MeatRecipeSerialiser<?>> RECIPE_SERIALISER = FabricRegistryBuilder.createSimple((Class<MeatRecipeSerialiser<?>>) (Class<?>) MeatRecipeSerialiser.class,
//            new Identifier(MeatLib.NAMESPACE, "recipe_serialiser")).buildAndRegister();

    public static <T extends Recipe<?>> RecipeSerializer<T> registerMeatlibSerializer(final String namespace, final String id, RecipeSerializer<T> serializer)
    {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(namespace, id), serializer);
    }

    public static <T extends Recipe<?>> RecipeType<T> registerType(final String namespace, final String id)
    {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(namespace, id), new RecipeType<T>()
        {
            public String toString()
            {
                return id;
            }
        });
    }

    public static <T extends MeatlibRecipe<?>> MeatRecipeType<T> registerMeatlibType(final String namespace, final String id)
    {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(namespace, id), new MeatRecipeType<T>()
        {
            public String toString()
            {
                return id;
            }
        });
    }

    public static <T extends MeatlibRecipe<?>> MeatRecipeSerialiser<T> registerMeatlibSerializer(final String namespace, final String id, MeatRecipeSerialiser<T> serializer)
    {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(namespace, id), serializer);
    }
}
