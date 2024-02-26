package com.neep.meatweapons;

import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.item.*;
import net.minecraft.item.Item;

public class MWItems
{
    public static Item BALLISTIC_CARTRIDGE = new BaseCraftingItem("ballistic_cartridge", 1, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item ENGINE = new BaseCraftingItem("engine", 0, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item FUSION_CANNON = new FusionCannonItem();
    public static Item HAND_CANNON = new HandCannonItem();
    public static Item MACHINE_PISTOL = new MachinePistolItem();
    public static Item LMG = new LMGItem();

    public static Item MA75 = new MA75Item();

//    public static Item BLASTER = new BlasterItem();
    public static Item HEAVY_CANNON = new HeavyCannonItem();
    public static Item AIRTRUCK_ITEM = new AirtruckItem("airtruck", TooltipSupplier.hidden(2), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static Item ASSAULT_DRILL = new AssaultDrillItem("assault_drill", 1000, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
}
