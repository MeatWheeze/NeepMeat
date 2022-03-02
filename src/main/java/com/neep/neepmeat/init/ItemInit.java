package com.neep.neepmeat.init;

import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.DaggerItem;
import com.neep.neepmeat.item.NMItem;
import com.neep.neepmeat.item.base.BaseCraftingItem;
import com.neep.neepmeat.item.base.TieredCraftingItemFactory;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemInit
{
    public static Map<String, NMItem> ITEMS = new LinkedHashMap<>();

    public static TieredCraftingItemFactory ROUGH = new TieredCraftingItemFactory(new String[]{"rough", "pristine"});
    public static TieredCraftingItemFactory CRUDE = new TieredCraftingItemFactory(new String[]{"crude", "adv"});

    public static Item SACRIFICIAL_DAGGER = new DaggerItem("sacrificial_dagger", new FabricItemSettings().group(NMItemGroups.GENERAL));
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

    public static void putItem(String id, NMItem item)
    {
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        ITEMS.put(id, item);
    }

    public static void registerItems()
    {
        for (NMItem item : ITEMS.values())
        {
            Registry.register(Registry.ITEM, new Identifier(NeepMeat.NAMESPACE, item.getRegistryName()), (Item) item);
        }
    }

    static
    {
//        ROUGH.get("flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//        ROUGH.get("reanimated_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
        ROUGH.get("brain", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
        CRUDE.get("integration_charge", true, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    }
}
