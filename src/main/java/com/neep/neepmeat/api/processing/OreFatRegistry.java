package com.neep.neepmeat.api.processing;

import com.google.common.collect.Maps;
import com.neep.meatlib.mixin.RecipeManagerAccessor;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.fluid.ore_fat.OreFatFluidFactory;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class OreFatRegistry implements SimpleSynchronousResourceReloadListener
{
    public static final OreFatRegistry INSTANCE = new OreFatRegistry();

    private final Map<Item, Entry> inputToEntry = Maps.newHashMap();
    private final Map<NbtCompound, Entry> nbtToEntry = Maps.newHashMap();

    public static void init()
    {
//        register(ConventionalItemTags.RAW_IRON_ORES, "Iron", 0xfedec8, Items.IRON_INGOT);
//        register(ConventionalItemTags.RAW_GOLD_ORES, "Gold", 0xfaea2e, Items.GOLD_INGOT);
//        register(ConventionalItemTags.RAW_COPPER_ORES, "Copper", 0x4fba98, Items.COPPER_INGOT);

//        if (FabricLoader.getInstance().isModLoaded("modern_industrialization"))
//        {
//            register(TagKey.of(Registries.ITEM.getKey(), new Identifier("c:raw_lead_ores")), "Lead", 0x7188ca, Registries.ITEM.get(new Identifier("modern_industrialization:lead_ingot")));
//            register(TagKey.of(Registries.ITEM.getKey(), new Identifier("c:raw_nickel_ores")), "Nickel", 0xe5e5b7, Registries.ITEM.get(new Identifier("modern_industrialization:nickel_ingot")));
//            register(TagKey.of(Registries.ITEM.getKey(), new Identifier("c:raw_silver_ores")), "Silver", 0x94aad3, Registries.ITEM.get(new Identifier("modern_industrialization:silver_ingot")));
//            register(TagKey.of(Registries.ITEM.getKey(), new Identifier("c:raw_tin_ores")), "Tin", 0xe2d9f2, Registries.ITEM.get(new Identifier("modern_industrialization:tin_ingot")));
//            register(TagKey.of(Registries.ITEM.getKey(), new Identifier("c:raw_antimony_ores")), "Antimony", 0x80808c, Registries.ITEM.get(new Identifier("modern_industrialization:antimony_ingot")));
//        }

        ServerLifecycleEvents.SERVER_STARTED.register(INSTANCE::generate);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> INSTANCE.generate(server));
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
                if (stack.isIn(ConventionalItemTags.RAW_ORES) || stack.isIn(ConventionalItemTags.ORES))
                {
                    registerRoute(ItemVariant.of(stack), ItemVariant.of(output));
                }
            }
        }
    }

    public void registerRoute(ItemVariant input, ItemVariant output)
    {
        NbtCompound nbt = createNbt(output.getItem());
        Entry entry = new Entry(output.getItem().getName(), 0, output, nbt);

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
        // TODO: Add disabling and extra entries here
    }

    public record Entry(Text name, int col, ItemVariant result, NbtCompound nbt)
    {
    }
}
