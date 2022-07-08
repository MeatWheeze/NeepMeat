package com.neep.neepmeat.init;

import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
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

    public static void init()
    {

    }

    static
    {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, tableBuilder, source) ->
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
        });
    }
}
