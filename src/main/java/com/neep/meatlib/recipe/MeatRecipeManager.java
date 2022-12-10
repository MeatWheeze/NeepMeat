package com.neep.meatlib.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.neep.meatlib.MeatLib;
import com.neep.meatlib.network.SyncMeatRecipesS2CPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.*;
import java.util.stream.Collectors;

public class MeatRecipeManager extends JsonDataLoader implements IdentifiableResourceReloadListener
{
    private static final MeatRecipeManager INSTANCE = new MeatRecipeManager();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private Map<MeatRecipeType<?>, Map<Identifier, MeatRecipe<?>>> recipes = ImmutableMap.of();
    private Map<Identifier, MeatRecipe<?>> recipesById = ImmutableMap.of();

    public MeatRecipeManager()
    {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), "special_recipes");
        System.out.println(GSON);
    }

    public static MeatRecipeManager getInstance()
    {
        return INSTANCE;
    }

    public Optional<? extends MeatRecipe<?>> get(Identifier id)
    {
        return Optional.ofNullable(this.recipesById.get(id));
    }

    public <C extends Inventory, T extends MeatRecipe<C>> Optional<T> getFirstMatch(MeatRecipeType<T> type, C context)
    {
        return this.getAllOfType(type).values().stream().flatMap(recipe -> type.match(recipe, context).stream()).findFirst();
    }

    private <C, T extends MeatRecipe<C>> Map<Identifier, MeatRecipe<C>> getAllOfType(MeatRecipeType<T> type)
    {
        // Say goodbye to type safety
        return (Map<Identifier, MeatRecipe<C>>) (Map<Identifier, ?>) this.recipes.getOrDefault(type, Collections.emptyMap());
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler)
    {
        HashMap<MeatRecipeType<?>, ImmutableMap.Builder<Identifier, MeatRecipe<?>>> map2 = Maps.newHashMap();
        ImmutableMap.Builder<Identifier, MeatRecipe<?>> builder = ImmutableMap.builder();
        for (Map.Entry<Identifier, JsonElement> entry2 : prepared.entrySet())
        {
            Identifier identifier = entry2.getKey();
            try
            {
                MeatRecipe<?> recipe = deserialize(identifier, JsonHelper.asObject(entry2.getValue(), "top element"));
                map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder()).put(identifier, recipe);
                builder.put(identifier, recipe);
            }
            catch (JsonParseException | IllegalArgumentException runtimeException)
            {
                MeatLib.LOGGER.error("Parsing error loading meatlib recipe {} ({})", identifier, runtimeException);
            }
        }
        this.recipes = map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> (entry.getValue()).build()));
        this.recipesById = builder.build();
        MeatLib.LOGGER.info("Loaded {} meatlib recipes", map2.size());

    }

    public static MeatRecipe<?> deserialize(Identifier id, JsonObject json)
    {
        String string = JsonHelper.getString(json, "type");
        return RecipeRegistry.RECIPE_SERIALISER.getOrEmpty(new Identifier(string)).orElseThrow(() ->
                new JsonSyntaxException("Invalid or unsupported meatlib recipe type '" + string + "'"))
                .read(id, json);
    }

    public Collection<MeatRecipe<?>> values()
    {
        return this.recipes.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
    }

    public void setRecipes(Iterable<MeatRecipe<?>> recipes)
    {
        HashMap<MeatRecipeType<?>, Map<Identifier, MeatRecipe<?>>> map = Maps.newHashMap();
        ImmutableMap.Builder<Identifier, MeatRecipe<?>> builder = ImmutableMap.builder();
        recipes.forEach(recipe ->
        {
            if (recipe == null) throw new IllegalStateException("Received recipe is null on the client. Is serialisation correctly implemented?");

            Map<Identifier, MeatRecipe<?>> map2 = map.computeIfAbsent(recipe.getType(), t -> Maps.newHashMap());
            Identifier identifier = recipe.getId();
            MeatRecipe<?> recipe2 = map2.put(identifier, recipe);
            builder.put(identifier, recipe);
            if (recipe2 != null)
            {
                throw new IllegalStateException("Duplicate meatlib recipe ignored with ID " + identifier);
            }
        });
        this.recipes = ImmutableMap.copyOf(map);
        this.recipesById = builder.build();
    }

    @Override
    public Identifier getFabricId()
    {
        return new Identifier(MeatLib.CURRENT_NAMESPACE, "special_recipes");
    }

    static
    {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
        {
            SyncMeatRecipesS2CPacket.send(player, getInstance().values());
        });
    }
}
