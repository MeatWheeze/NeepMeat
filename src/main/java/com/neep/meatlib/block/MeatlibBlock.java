package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.SelfRegisterable;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface MeatlibBlock extends ItemConvertible, SelfRegisterable
{
    @Override
    default void register(Identifier id)
    {
        Registry.register(Registries.BLOCK, id, (Block) this);
    }

    default boolean autoGenDrop()
    {
        return true;
    }

    default ItemConvertible dropsLike()
    {
        return this;
    }

    @Nullable
    default LootTable.Builder genLoot(BlockLootTableGenerator generator)
    {
        return null;
    }

    @FunctionalInterface
    interface ItemFactory
    {
        BlockItem create(Block block, RegistrationContext ctx, ItemSettings settings);
    }
}
