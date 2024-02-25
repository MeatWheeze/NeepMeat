package com.neep.neepmeat.init;

import com.neep.meatlib.block.BaseCropBlock;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class NMLootTables
{
    private static final Identifier ZOMBIE = EntityType.ZOMBIE.getLootTableId();
    private static final Identifier ELDER_GUARDIAN = EntityType.ELDER_GUARDIAN.getLootTableId();
    private static final Identifier DUNGEON = LootTables.SIMPLE_DUNGEON_CHEST;
    private static final Identifier DESERT_TEMPLE = LootTables.DESERT_PYRAMID_CHEST;
    private static final Identifier JUNGLE_TEMPLE = LootTables.JUNGLE_TEMPLE_CHEST;
    private static final Identifier ARMORER_CHEST = LootTables.VILLAGE_ARMORER_CHEST;
    private static final Identifier BUTCHER_CHEST = LootTables.VILLAGE_BUTCHER_CHEST;
    private static final Identifier GRASS = Blocks.GRASS.getLootTableId();
    private static final Identifier TALL_GRASS = Blocks.TALL_GRASS.getLootTableId();
    private static final Identifier POTATOES = Blocks.POTATOES.getLootTableId();

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
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f))))
                                .apply(LootingEnchantLootFunction.builder(ConstantLootNumberProvider.create(1.0f)))
                        ).conditionally(RandomChanceLootCondition.builder(0.25f));
//                        .conditionally(KilledByPlayerLootCondition.builder());

                tableBuilder.pool(poolBuilder);
            }
            else if (ELDER_GUARDIAN.equals(id))
            {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(((LeafEntry.Builder<?>)ItemEntry.builder(NMBlocks.STATUE.asItem())
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f))))
                                .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f))))
                        .conditionally(KilledByPlayerLootCondition.builder());

                tableBuilder.pool(poolBuilder);
            }
            else if (DUNGEON.equals(id) || DESERT_TEMPLE.equals(id) || ARMORER_CHEST.equals(id) || BUTCHER_CHEST.equals(id) || JUNGLE_TEMPLE.equals(id))
            {
                LootPool.Builder builder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0.0f, 1.0f))
                    .with(ItemEntry.builder(NMBlocks.INTEGRATOR_EGG.asItem()).weight(10));

                tableBuilder.pool(builder);
            }
            else if (GRASS.equals(id))
            {
                LootPool.Builder builder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.025f))
                        .with(ItemEntry.builder(((BaseCropBlock) NMBlocks.WHISPER_WHEAT).getSeedsItem()).weight(1));

                tableBuilder.pool(builder);
            }
            else if (TALL_GRASS.equals(id))
            {
                LootPool.Builder builder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.025f))
                        .with(ItemEntry.builder(((BaseCropBlock) NMBlocks.WHISPER_WHEAT).getSeedsItem()).weight(1));

                tableBuilder.pool(builder);
            }
            else if (POTATOES.equals(id))
            {
                LootPool.Builder builder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.04f))
                        .with(ItemEntry.builder(((BaseCropBlock) NMBlocks.FLESH_POTATO).getSeedsItem()).weight(1));

                tableBuilder.pool(builder);
            }
        });
    }
}
