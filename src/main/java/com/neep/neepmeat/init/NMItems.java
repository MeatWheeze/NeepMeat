package com.neep.neepmeat.init;

import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.item.CheeseCleaverItem;
import com.neep.neepmeat.item.DaggerItem;
import com.neep.neepmeat.item.SlasherItem;
import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.item.TieredCraftingItemFactory;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

@SuppressWarnings("unused")
public class NMItems
{

    public static TieredCraftingItemFactory ROUGH = new TieredCraftingItemFactory(new String[]{"rough", "pristine"});
    public static TieredCraftingItemFactory CRUDE = new TieredCraftingItemFactory(new String[]{"crude", "adv"});

    public static Item SACRIFICIAL_DAGGER = new DaggerItem("sacrificial_dagger", new FabricItemSettings().group(NMItemGroups.WEAPONS));
    public static Item CHEESE_CLEAVER = new CheeseCleaverItem("cheese_cleaver", new FabricItemSettings().group(NMItemGroups.WEAPONS));
    public static Item SLASHER = new SlasherItem("slasher", new FabricItemSettings().group(NMItemGroups.WEAPONS));

    public static Item ANIMAL_HEART = new BaseCraftingItem("animal_heart", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REANIMATED_HEART = new BaseCraftingItem("reanimated_heart", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item COPPER_COIL = new BaseCraftingItem("copper_coil", false, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item BIOELECTRIC_ORGAN = new BaseCraftingItem("bioelectric_organ", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    // Made from machine fluid
    public static Item MACHINE_FLUID = new BaseCraftingItem("machine_fluid", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    // Organism Parts
    public static Item DIGESTIVE_SYSTEM = new BaseCraftingItem("digestive_system", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item INTERFACE_PORTS = new BaseCraftingItem("interface_array", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item INTERNAL_COMPONENTS = new BaseCraftingItem("internal_components", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item GANGLIAL_CENTRE = new BaseCraftingItem("ganglial_cluster", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    static
    {
//        ROUGH.get("flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//        ROUGH.get("reanimated_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
        ROUGH.get("brain", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
        CRUDE.get("integration_charge", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    }
}
