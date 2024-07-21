package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public class BaseSlabBlock extends SlabBlock implements MeatlibBlock
{
    protected BlockItem blockItem;

    public BaseSlabBlock(RegistrationContext ctx, BlockState baseBlockState, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, ctx, itemSettings);
    }

    @Override
    public LootTable.@Nullable Builder genLoot(BlockLootTableGenerator generator)
    {
        return generator.slabDrops(this);
    }
}
