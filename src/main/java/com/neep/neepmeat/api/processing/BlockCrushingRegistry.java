package com.neep.neepmeat.api.processing;

import com.google.common.collect.Maps;
import com.neep.meatlib.api.event.DataPackPostProcess;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.mixin.loot.CombinedEntryAccessor;
import com.neep.neepmeat.mixin.loot.ItemEntryAccessor;
import com.neep.neepmeat.recipe.AdvancedBlockCrushingRecipe;
import com.neep.neepmeat.recipe.BlockCrushingRecipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class BlockCrushingRegistry
{
    public static BlockCrushingRegistry INSTANCE;

    public static void init()
    {
        DataPackPostProcess.AFTER_DATA_PACK_LOAD.register(BlockCrushingRegistry::reload);
    }

    private static void reload(MinecraftServer server)
    {
        INSTANCE = new BlockCrushingRegistry(server);
    }

    private final Map<ItemVariant, Entry> basicInputToEntry = Maps.newHashMap();
    private final Map<ItemVariant, Entry> advancedInputToEntry = Maps.newHashMap();

    // Either of these can be disabled by removing the JSON- hang on, that won't work. Erm...
    @Nullable private final BlockCrushingRecipe blockCrushingRecipe;
    @Nullable private final AdvancedBlockCrushingRecipe advancedBlockCrushingRecipe;

    private BlockCrushingRegistry(MinecraftServer server)
    {
        this.blockCrushingRecipe = BlockCrushingRecipe.get(server.getRecipeManager());
        this.advancedBlockCrushingRecipe = AdvancedBlockCrushingRecipe.get(server.getRecipeManager());

        searchForLootTables(server);
    }

    @Nullable
    public Entry getFromInputBasic(ItemVariant input)
    {
        return basicInputToEntry.get(input);
    }

    @Nullable
    public Entry getFromInputAdvanced(ItemVariant input)
    {
        return advancedInputToEntry.get(input);
    }

    private void searchForLootTables(MinecraftServer server)
    {
        NeepMeat.LOGGER.info("Searching for block crushing loot tables");

        LootManager lootManager = server.getLootManager();

        TagKey<Block> inputs = NMTags.BLOCK_CRUSHING_INPUTS;

        Registries.ITEM.stream()
                .filter(i -> i instanceof BlockItem)
                .map(i -> (BlockItem) i)
                .filter(i -> i.getBlock().getRegistryEntry().isIn(inputs))
                .forEach(i -> inspectLootTable(lootManager, i));
        ;
    }

    /**
     * Incredibly cursed traversal that looks for a structure that roughly matches that of a vanilla ore.
     */
    private void inspectLootTable(LootManager lootManager, BlockItem blockItem)
    {
        if (advancedBlockCrushingRecipe == null && blockCrushingRecipe == null)
            return;

        TagKey<Item> outputs = NMTags.BLOCK_CRUSHING_OUTPUTS;

        LootTable lootTable = lootManager.getLootTable(blockItem.getBlock().getLootTableId());
        for (LootPool pool : lootTable.pools)
        {
            for (LootPoolEntry entry : pool.entries)
            {
                if (entry instanceof AlternativeEntry alternativeEntry)
                {
                    for (LootPoolEntry child : ((CombinedEntryAccessor) alternativeEntry).getChildren())
                    {
                        if (child instanceof ItemEntry itemEntry)
                        {
                            Item outputItem = ((ItemEntryAccessor) itemEntry).getItem();
                            boolean isRawOre = outputItem.getRegistryEntry().isIn(outputs);
                            if (isRawOre)
                            {
                                register(blockItem, outputItem); // baseAmount should be extracted from the loot table, but I've no idea how.
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void register(Item inputVariant, Item outputItem)
    {
        if (blockCrushingRecipe != null)
        {
            basicInputToEntry.put(ItemVariant.of(inputVariant), create(inputVariant, outputItem, blockCrushingRecipe));
        }

        if (advancedBlockCrushingRecipe != null)
        {
            advancedInputToEntry.put(ItemVariant.of(inputVariant), create(inputVariant, outputItem, advancedBlockCrushingRecipe));
        }
    }

    private Entry create(Item inputItem, Item outputItem, BlockCrushingRecipe recipe)
    {
        int baseAmount = (int) recipe.getBaseAmount();
        int extraAmount = (int) recipe.getExtraAmount();

        RecipeInput<Item> input = RecipeInputs.of(inputItem, 1);
        RecipeOutput<Item> output = new RecipeOutputImpl<>(outputItem, baseAmount, baseAmount, 1);
        RecipeOutput<Item> extra = new RecipeOutputImpl<>(outputItem, extraAmount, extraAmount, 0.5f);

        return new Entry(input, output, extra);
    }

    public Collection<Entry> getBasicEntries()
    {
        return basicInputToEntry.values();
    }

    public Collection<Entry> getAdvancedEntries()
    {
        return advancedInputToEntry.values();
    }

    public record Entry(RecipeInput<Item> input, RecipeOutput<Item> output, RecipeOutput<Item> extra)
    {

    }
}
