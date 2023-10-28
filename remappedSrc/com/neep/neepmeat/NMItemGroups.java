package com.neep.neepmeat;

import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.transport.FluidTransport;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NMItemGroups
{
    public static final ItemGroup GENERAL = FabricItemGroup.builder(new Identifier(NeepMeat.NAMESPACE, "general"))
            .icon(() -> new ItemStack(FluidTransport.PUMP)).build();

    public static final ItemGroup INGREDIENTS = FabricItemGroup.builder( new Identifier(NeepMeat.NAMESPACE, "ingredients"))
            .icon(() -> new ItemStack(NMItems.INTERNAL_COMPONENTS)).build();

    public static final ItemGroup FOOD = FabricItemGroup.builder( new Identifier(NeepMeat.NAMESPACE, "food"))
            .icon(() -> new ItemStack(NMItems.COOKED_MEAT_BRICK)).build();

}
