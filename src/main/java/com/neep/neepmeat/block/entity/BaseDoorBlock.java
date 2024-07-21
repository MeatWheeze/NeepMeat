package com.neep.neepmeat.block.entity;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public class BaseDoorBlock extends DoorBlock implements MeatlibBlock
{
    public BaseDoorBlock(RegistrationContext ctx, Settings settings, ItemSettings itemSettings, BlockSetType blockSetType)
    {
        super(settings, blockSetType);
        ctx.append(this, itemSettings.create(this, ctx, itemSettings));
    }

    @Override
    public @Nullable LootTable.Builder genLoot(BlockLootTableGenerator generator)
    {
        return generator.doorDrops(this);
    }
}
