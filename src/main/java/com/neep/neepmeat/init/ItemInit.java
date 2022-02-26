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

import java.util.HashMap;
import java.util.Map;

public class ItemInit
{
    public static Map<String, NMItem> ITEMS = new HashMap<>();

    public static Item SACRIFICIAL_DAGGER = new DaggerItem("sacrificial_dagger", new FabricItemSettings().group(NMItemGroups.GENERAL));

    public static TieredCraftingItemFactory CRAFTING = new TieredCraftingItemFactory(new String[]{"rough", "pristine"});

    public static Item ANIMAL_HEART = new BaseCraftingItem("animal_heart", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REANIMATED_HEART = new BaseCraftingItem("reanimated_heart", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

//    public static Item ROUGH_FLESH = new BaseCraftingItem("rough_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item CLEAN_FLESH = new BaseCraftingItem("clean_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item PRISTINE_FLESH = new BaseCraftingItem("pristine_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

//    public static Item ROUGH_REANIMATED_FLESH = new BaseCraftingItem("rough_reanimated_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item CLEAN_REANIMATED_FLESH = new BaseCraftingItem("clean_reanimated_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item PRISTINE_REANIMATED_FLESH = new BaseCraftingItem("pristine_reanimated_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    public static Item DIGESTIVE_SYSTEM = new BaseCraftingItem("digestive_system", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item GANGLIAL_CENTRE = new BaseCraftingItem("ganglial_cluster", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

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
        CRAFTING.get("flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
        CRAFTING.get("reanimated_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
        CRAFTING.get("brain", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    }
}
