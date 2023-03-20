package com.neep.neepmeat;

import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.transport.FluidTransport;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NMItemGroups
{
    public static final ItemGroup GENERAL = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "general"),
            () -> new ItemStack(FluidTransport.PUMP));

    public static final ItemGroup INGREDIENTS = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "ingredients"),
            () -> new ItemStack(NMItems.INTERNAL_COMPONENTS));

    public static final ItemGroup FOOD = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "food"),
            () -> new ItemStack(NMItems.COOKED_MEAT_BRICK));

}
