package com.neep.neepmeat.init;

import com.neep.meatlib.block.BaseCropBlock;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.Map;

public class NMLootTables
{
    private static final Identifier ZOMBIE = EntityType.ZOMBIE.getLootTableId();
    private static final Identifier DUNGEON = LootTables.SIMPLE_DUNGEON_CHEST;
    private static final Identifier GRASS = Blocks.GRASS.getLootTableId();
    private static final Identifier TALL_GRASS = Blocks.TALL_GRASS.getLootTableId();

    public static void init()
    {

    }

    static
    {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
        {
            if (ZOMBIE.equals(id))
            {
                LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(NMItems.ROUGH_BRAIN)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f))))
                                .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f))))
                        .conditionally(KilledByPlayerLootCondition.builder());

                tableBuilder.pool(poolBuilder);
            }
            if (DUNGEON.equals(id))
            {
                LootPool.Builder builder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0.0f, 1.0f))
                    .with(ItemEntry.builder(NMBlocks.INTEGRATOR_EGG.asItem()).weight(10));

                tableBuilder.pool(builder);
            }
            if (GRASS.equals(id))
            {
                LootPool.Builder builder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.125f))
                        .with(ItemEntry.builder(((BaseCropBlock) NMBlocks.WHISPER_WHEAT).getSeedsItem()).weight(1));

                tableBuilder.pool(builder);
            }
            if (TALL_GRASS.equals(id))
            {
                LootPool.Builder builder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.125f))
                        .with(ItemEntry.builder(((BaseCropBlock) NMBlocks.WHISPER_WHEAT).getSeedsItem()).weight(1));

                tableBuilder.pool(builder);
            }
        });
    }
}
