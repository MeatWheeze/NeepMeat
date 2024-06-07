package com.neep.neepmeat.api.processing;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.*;
import com.neep.meatlib.api.event.DataPackPostProcess;
import com.neep.meatlib.mixin.RecipeManagerAccessor;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.fluid.ore_fat.OreFatFluidFactory;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class OreFatRegistry implements SimpleSynchronousResourceReloadListener
{
    public static final OreFatRegistry INSTANCE = new OreFatRegistry();

    private final Set<TagKey<Item>> generateForTags = Sets.newHashSet();
    private final Set<Identifier> generateForItems = Sets.newHashSet();

    private final Map<Item, Entry> inputToEntry = Maps.newHashMap();
    private final Map<NbtCompound, Entry> nbtToEntry = Maps.newHashMap();

    public static void init()
    {
        DataPackPostProcess.EVENT.register(INSTANCE::generate);
    }

    private void addTag(Identifier id)
    {
        generateForTags.add(TagKey.of(Registry.ITEM.getKey(), id));
    }

    private void generate(MinecraftServer server)
    {
        inputToEntry.clear();
        nbtToEntry.clear();

        RecipeManagerAccessor manager = (RecipeManagerAccessor) server.getRecipeManager();
        Map<Identifier, SmeltingRecipe> smeltingRecipes = manager.callGetAllOfType(RecipeType.SMELTING);
        smeltingRecipes.forEach((id, recipe) ->
        {
            List<Ingredient> ingredients = recipe.getIngredients();
            ItemStack output = recipe.getOutput();
            INSTANCE.testIngredient(ingredients, output);
        });

        NeepMeat.LOGGER.info("Generated {} ore fat routes", inputToEntry.size());
    }

    /**
     * Runs through the inputs and checks if they are raw ores.
     * If this is the case, it is probably safe to generate an ore fat path from input to output.
     */
    private void testIngredient(Collection<Ingredient> input, ItemStack output)
    {
        if (output.isEmpty())
            return;

        if (input.isEmpty())
            return;

        for (Ingredient ingredient : input)
        {
            if (ingredient.isEmpty())
                continue;

            for (ItemStack stack : ingredient.getMatchingStacks())
            {
                Identifier itemId = stack.getItem().getRegistryEntry().registryKey().getValue();
                if (stack.streamTags().anyMatch(generateForTags::contains) || generateForItems.contains(itemId))
                {
                    registerRoute(ItemVariant.of(stack), ItemVariant.of(output));
                }
            }
        }
    }

    public void registerRoute(ItemVariant input, ItemVariant output)
    {
        NbtCompound nbt = createNbt(output.getItem());
        Entry entry = new Entry(output.getItem().getName(), output, nbt);

        inputToEntry.putIfAbsent(input.getItem(), entry);
        nbtToEntry.put(nbt, entry);
    }

    /**
     * @param outputItem Output item
     * @return The NBT compound that held by all dirty and clean FluidVariants corresponding to the item.
     */
    private NbtCompound createNbt(Item outputItem)
    {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("item", Registry.ITEM.getId(outputItem).toString());
        return nbtCompound;
    }

    @Nullable
    public static Entry get(NbtCompound nbt)
    {
        return INSTANCE.nbtToEntry.get(nbt);
    }

    @Nullable
    public static Entry getFromInput(Item item)
    {
        return INSTANCE.inputToEntry.get(item);
    }

    @Nullable
    public static Entry getFromVariant(FluidVariant variant)
    {
        if (variant.getObject() instanceof OreFatFluidFactory.Main)
        {
            NbtCompound nbt = variant.getNbt();
            return INSTANCE.nbtToEntry.get(nbt);
        }
        return null;
    }

    public Item getItem(FluidVariant variant)
    {
        NbtCompound nbt = variant.getNbt();
        if (variant.getObject() instanceof OreFatFluidFactory.Main && nbt != null)
        {
            return Registry.ITEM.get(new Identifier((nbt.getString("item"))));
        }
        return null;
    }

    public FluidVariant getDirty(Item ore)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item", Registry.ITEM.getId(ore).toString());
        return FluidVariant.of(NMFluids.STILL_DIRTY_ORE_FAT, nbt);
    }

    @Nullable
    public static FluidVariant getClean(FluidVariant dirty)
    {
        if (dirty.getFluid() instanceof OreFatFluidFactory.Main)
        {
            NbtCompound nbtCompound = dirty.getNbt();
            return FluidVariant.of(NMFluids.STILL_CLEAN_ORE_FAT, nbtCompound);
        }
        return null;
    }

    @Override
    public Identifier getFabricId()
    {
        return new Identifier(NeepMeat.NAMESPACE, "ore_fat");
    }

    @Override
    public void reload(ResourceManager manager)
    {

        for (Identifier id : manager.findResources("ore_fat", path -> path.getPath().endsWith(".json")).keySet())
        {
            if (manager.getResource(id).isPresent())
            {
                // Last file replaces previous ones
                generateForTags.clear();
                generateForItems.clear();;

                try (InputStream stream = manager.getResource(id).get().getInputStream())
                {
                    Reader reader = new InputStreamReader(stream);
                    JsonElement rootElement = JsonParser.parseReader(reader);
                    JsonObject rootObject = (JsonObject) rootElement;

                    // Generate routes for smelting recipes whose inputs are in these tags
                    JsonArray generateForTags = JsonHelper.getArray(rootObject, "generate_for_tags");
                    generateForTags.forEach(e ->
                    {
                        Identifier tagId = Identifier.tryParse(e.getAsString());
                        addTag(tagId);
                    });

                    JsonArray generateForItems = JsonHelper.getArray(rootObject, "generate_for_items");
                    generateForItems.forEach(e ->
                    {
                        Identifier itemId = Identifier.tryParse(e.getAsString());

                        this.generateForItems.add(itemId);
                    });
                }
                catch (Exception e)
                {
                    NeepMeat.LOGGER.error("Error while reading ore fat json " + id.toString(), e);
                }
            }
        }

    }

    public record Entry(Text name, ItemVariant result, NbtCompound nbt)
    {
    }
}
