package com.neep.neepmeat;

import com.neep.neepmeat.transport.FluidTransport;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NMItemGroups
{
//    public static final ItemGroup GENERAL = FabricItemGroup.builder(new Identifier(NeepMeat.NAMESPACE, "general"))
//            .icon(() -> new ItemStack(FluidTransport.PUMP)).build();
//
//    public static final ItemGroup INGREDIENTS = FabricItemGroup.builder( new Identifier(NeepMeat.NAMESPACE, "ingredients"))
//            .icon(() -> new ItemStack(NMItems.INTERNAL_COMPONENTS)).build();
//
//    public static final ItemGroup FOOD = FabricItemGroup.builder( new Identifier(NeepMeat.NAMESPACE, "food"))
//            .icon(() -> new ItemStack(NMItems.COOKED_MEAT_BRICK)).build();

    public static final ItemGroup GENERAL = FabricItemGroup.builder().icon(() -> FluidTransport.PUMP.asItem().getDefaultStack()).build();

    public static final ItemGroup INGREDIENTS = GENERAL;

    public static final ItemGroup FOOD = GENERAL;

//    public static final ItemGroup INGREDIENTS = FabricItemGroupBuilder.build( new Identifier(NeepMeat.NAMESPACE, "ingredients"),
//            () -> new ItemStack(NMItems.INTERNAL_COMPONENTS));
//
//    public static final ItemGroup FOOD = FabricItemGroupBuilder.build( new Identifier(NeepMeat.NAMESPACE, "food"),
//            () -> new ItemStack(NMItems.COOKED_MEAT_BRICK));

    public static void init()
    {
        Registry.register(Registries.ITEM_GROUP, new Identifier(NeepMeat.NAMESPACE, "general"), GENERAL);
    }
}
