package com.neep.neepmeat.api.processing;

import com.neep.meatlib.api.event.DataPackPostProcess;
import com.neep.meatlib.network.PacketBufUtil;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockCrushingRegistry
{
    public static final Identifier CHANNEL_ID = new Identifier(NeepMeat.NAMESPACE, "block_crushing_sync");

    public static final BlockCrushingRegistry INSTANCE = new BlockCrushingRegistry();

    private final Map<ItemVariant, Entry> basicInputToEntry = new HashMap<>();
    private final Map<ItemVariant, Entry> advancedInputToEntry = new HashMap<>();

    // Either of these can be disabled by removing the JSON- hang on, that won't work. Erm...
    @Nullable private BlockCrushingRecipe blockCrushingRecipe;
    @Nullable private AdvancedBlockCrushingRecipe advancedBlockCrushingRecipe;

    private BlockCrushingRegistry() { }

    public void read(RecipeManager manager, PacketByteBuf buf)
    {
        this.blockCrushingRecipe = BlockCrushingRecipe.get(manager);
        this.advancedBlockCrushingRecipe = AdvancedBlockCrushingRecipe.get(manager);

        PacketBufUtil.readMap(buf, basicInputToEntry::put, ItemVariant::fromPacket, Entry::read);
        PacketBufUtil.readMap(buf, advancedInputToEntry::put, ItemVariant::fromPacket, Entry::read);
    }

    private void write(PacketByteBuf buf)
    {
        PacketBufUtil.writeMap(buf, basicInputToEntry, TransferVariant::toPacket, Entry::write);
        PacketBufUtil.writeMap(buf, advancedInputToEntry, TransferVariant::toPacket, Entry::write);
    }

    public static void init()
    {
        DataPackPostProcess.AFTER_DATA_PACK_LOAD.register(BlockCrushingRegistry.INSTANCE::reload);
        DataPackPostProcess.SYNC.register(BlockCrushingRegistry::sync);
    }

    private void reload(MinecraftServer server)
    {
        RecipeManager recipeManager = server.getRecipeManager();
        LootManager lootManager = server.getLootManager();
        this.blockCrushingRecipe = BlockCrushingRecipe.get(recipeManager);
        this.advancedBlockCrushingRecipe = AdvancedBlockCrushingRecipe.get(recipeManager);

        searchForLootTables(lootManager);
    }

    private static void sync(MinecraftServer server, @NotNull Set<ServerPlayerEntity> players)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        INSTANCE.write(buf);
        for (var player : players)
        {
            ServerPlayNetworking.send(player, CHANNEL_ID, buf);
        }
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

    private void searchForLootTables(LootManager lootManager)
    {
        NeepMeat.LOGGER.info("Searching for block crushing loot tables");

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
        public static Entry read(PacketByteBuf buf)
        {
            RecipeInput<Item> input = RecipeInput.fromBuffer(buf);
            RecipeOutput<Item> output = RecipeOutputImpl.fromBuffer(Registries.ITEM, buf);
            RecipeOutput<Item> extra = RecipeOutputImpl.fromBuffer(Registries.ITEM, buf);

            return new Entry(input, output, extra);
        }

        public void write(PacketByteBuf buf)
        {
            input.write(buf);
            output.write(Registries.ITEM, buf);
            extra.write(Registries.ITEM, buf);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void onPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
        {
            if (client.world != null)
            {
                // Hopefully this can be done on the netty thread
                BlockCrushingRegistry.INSTANCE.read(client.world.getRecipeManager(), buf);
//                BlockCrushingRegistry registry = BlockCrushingRegistry.read(client.world.getRecipeManager(), buf);
//                client.execute(() ->
//                {
//                });
            }
        }
    }
}
