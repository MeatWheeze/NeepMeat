package com.neep.neepmeat;

import com.neep.neepmeat.init.BlockInitialiser;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NMItemGroups
{
    public static final ItemGroup GENERAL = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "general"),
            () -> new ItemStack(BlockInitialiser.PUMP));

    public static final ItemGroup INGREDIENTS = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "ingredients"),
            () -> new ItemStack(Blocks.COBBLESTONE));
}
