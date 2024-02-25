package com.neep.meatlib.recipe;

import com.neep.meatlib.mixin.RecipeManagerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A custom interface for RecipeManager.
 * Advantages:
 * - Block entities can load their recipes without access to a World instance.
 * - MeatlibRecipe does not have its generic bounded by Inventory.
 * - No need to override and implement the irrelevant, useless methods in Recipe
 * - That's about it, I suppose.
 * - It's slightly cursed.
 */
public interface MeatlibRecipes
{
    static void init()
    {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
        {
            MeatlibRecipesImpl.INSTANCE = new MeatlibRecipesImpl(server::getRecipeManager);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
        {
            MeatlibRecipesImpl.INSTANCE = EMPTY;
        });
    }

    @Environment(EnvType.CLIENT)
    static void initClient()
    {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
        {
            if (!client.isIntegratedServerRunning())
            {
                MeatlibRecipesImpl.INSTANCE = new MeatlibRecipesImpl(() -> client.getNetworkHandler().getRecipeManager());
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
        {
            if (!client.isInSingleplayer())
            {
                MeatlibRecipesImpl.INSTANCE = EMPTY;
            }
        });
    }

    static MeatlibRecipes getInstance()
    {
        return MeatlibRecipesImpl.INSTANCE;
    }

    Optional<? extends MeatlibRecipe<?>> get(Identifier id);

    <C, T extends MeatlibRecipe<C>> Optional<T> get(RecipeType<T> type, Identifier id);

    <C, T extends MeatlibRecipe<C>> Optional<T> getFirstMatch(MeatRecipeType<T> type, C context);

    <C, T extends MeatlibRecipe<C>> Map<Identifier, T> getAllOfTypeSafe(MeatRecipeType<T> type);

    <C, T extends MeatlibRecipe<C>> Stream<T> getAllValuesOfType(MeatRecipeType<T> type);

    MeatlibRecipes EMPTY = new MeatlibRecipes()
    {
        @Override
        public Optional<? extends MeatlibRecipe<?>> get(Identifier id)
        {
            return Optional.empty();
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Optional<T> get(RecipeType<T> type, Identifier id)
        {
            return Optional.empty();
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Optional<T> getFirstMatch(MeatRecipeType<T> type, C context)
        {
            return Optional.empty();
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Map<Identifier, T> getAllOfTypeSafe(MeatRecipeType<T> type)
        {
            return Map.of();
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Stream<T> getAllValuesOfType(MeatRecipeType<T> type)
        {
            return Stream.empty();
        }
    };


    class MeatlibRecipesImpl implements MeatlibRecipes
    {

        public static MeatlibRecipes INSTANCE = EMPTY;
        private final Supplier<RecipeManager> recipeManager;

        private MeatlibRecipesImpl(Supplier<RecipeManager> recipeManagerSupplier)
        {
            this.recipeManager = recipeManagerSupplier;
        }

        @Override
        public Optional<? extends MeatlibRecipe<?>> get(Identifier id)
        {
            var recipe = recipeManager.get().get(id);
            if (recipe.isPresent() && recipe.get() instanceof MeatlibRecipe<?> mlr)
            {
                return Optional.of(mlr);
            }

            return Optional.empty();
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Optional<T> get(RecipeType<T> type, Identifier id)
        {
            var recipe = ((RecipeManagerAccessor) recipeManager.get()).getRecipesById().get(id);
            if (recipe == null || recipe.getType() != type)
                return Optional.empty();

            return Optional.ofNullable((T) recipe);
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Optional<T> getFirstMatch(MeatRecipeType<T> type, C context)
        {
            return getAllValuesOfType(type)
                    .flatMap(recipe -> type.match(recipe, context).stream()).findFirst();
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Map<Identifier, T> getAllOfTypeSafe(MeatRecipeType<T> type)
        {
            var map = ((RecipeManagerAccessor) recipeManager.get()).getRecipes().getOrDefault(type, Collections.emptyMap());

            return map.entrySet().stream()
                    .filter(e -> e.getValue() instanceof MeatlibRecipe<?>)
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (T) e.getValue()));
        }

        @Override
        public <C, T extends MeatlibRecipe<C>> Stream<T> getAllValuesOfType(MeatRecipeType<T> type)
        {
            var map = ((RecipeManagerAccessor) recipeManager.get()).getRecipes().getOrDefault(type, Collections.emptyMap());

            return map.values().stream()
                    .filter(recipe -> recipe instanceof MeatlibRecipe<?>) // Just in case something wEiRd has been done by another mod
                    .map(recipe -> (T) recipe);
        }
    }
}
