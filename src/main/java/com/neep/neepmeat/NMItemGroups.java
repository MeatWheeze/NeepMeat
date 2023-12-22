package com.neep.neepmeat;

import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NMItemGroups
{
    public static final ItemGroup GENERAL = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "general"),
            () -> new ItemStack(NMBlocks.PUMP));

    public static final ItemGroup INGREDIENTS = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "ingredients"),
            () -> new ItemStack(NMItems.INTERNAL_COMPONENTS));

    public static final ItemGroup WEAPONS = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "weapons"),
            () -> new ItemStack(NMItems.SLASHER));
}
