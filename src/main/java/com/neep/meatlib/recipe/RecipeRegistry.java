package com.neep.meatlib.recipe;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class RecipeRegistry
{
    public static SimpleRegistry<MeatRecipeType<?>> RECIPE_TYPE = FabricRegistryBuilder.createSimple((Class<MeatRecipeType<?>>) (Class<?>) MeatRecipeType.class,
            new Identifier(MeatLib.NAMESPACE, "recipe_type")).buildAndRegister();

    public static SimpleRegistry<MeatRecipeSerialiser<?>> RECIPE_SERIALISER = FabricRegistryBuilder.createSimple((Class<MeatRecipeSerialiser<?>>) (Class<?>) MeatRecipeSerialiser.class,
            new Identifier(MeatLib.NAMESPACE, "recipe_serialiser")).buildAndRegister();

    public static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(final String namespace, final String id, RecipeSerializer<T> serializer)
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

    public static <T extends MeatRecipe<?>> MeatRecipeType<T> registerSpecialType(final String namespace, final String id)
    {
        return Registry.register(RECIPE_TYPE, new Identifier(namespace, id), new MeatRecipeType<T>()
        {
            public String toString()
            {
                return id;
            }
        });
    }

    public static <T extends MeatRecipe<?>> MeatRecipeSerialiser<T> registerSerializer(final String namespace, final String id, MeatRecipeSerialiser<T> serializer)
    {
        return Registry.register(RECIPE_SERIALISER, new Identifier(namespace, id), serializer);
    }
}
