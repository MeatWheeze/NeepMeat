package com.neep.neepmeat;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroups
{
    public static final ItemGroup GENERAL = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "general"),
            () -> new ItemStack(Blocks.COBBLESTONE));
}
