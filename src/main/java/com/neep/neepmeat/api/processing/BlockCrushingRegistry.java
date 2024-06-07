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

import java.util.Map;

public class BlockCrushingRegistry
{
    public static final BlockCrushingRegistry INSTANCE = new BlockCrushingRegistry();

    private final Map<ItemVariant, Entry> inputToEntry = Maps.newHashMap();

    public static void init()
    {
        DataPackPostProcess.EVENT.register(INSTANCE::searchForLootTables);
    }

    @Nullable
    public Entry getFromInput(ItemVariant input)
    {
        return inputToEntry.get(input);
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

    private void inspectLootTable(LootManager lootManager, BlockItem blockItem)
    {
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
//                                NeepMeat.LOGGER.info("FOUND RAW ORES: {} - {}", blockItem, outputItem);
                                register(blockItem, outputItem, 1); // baseAmount should be extracted from the loot table, but I've no idea how.
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void register(Item inputVariant, Item outputItem, int baseAmount)
    {
        RecipeInput<Item> input = RecipeInputs.of(inputVariant, 1);
        RecipeOutput<Item> output = new RecipeOutputImpl<>(outputItem, baseAmount * 2, baseAmount * 2, 1);
        RecipeOutput<Item> extra = new RecipeOutputImpl<>(outputItem, baseAmount, baseAmount, 0.5f);
        inputToEntry.put(ItemVariant.of(inputVariant), new Entry(input, output, extra));
    }

    public record Entry(RecipeInput<Item> input, RecipeOutput<Item> output, RecipeOutput<Item> extra)
    {

    }
}
