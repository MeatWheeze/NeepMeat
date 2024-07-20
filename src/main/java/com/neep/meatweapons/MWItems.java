package com.neep.meatweapons;

import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.implant.BloodBulletProviderImplant;
import com.neep.meatweapons.item.*;
import com.neep.meatweapons.item.meatgun.MeatgunModuleItem;
import com.neep.meatweapons.item.meatgun.MeatgunPistolItem;
import com.neep.meatweapons.item.meatgun.MeatgunStaffItem;
import com.neep.meatweapons.meatgun.module.MeatgunModules;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.item.PlayerImplantItem;
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

    public static Item MEATGUN_PISTOL = ItemRegistry.queue(new MeatgunPistolItem("meatgun_pistol", TooltipSupplier.simple(1), new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static Item MEATGUN_STAFF = ItemRegistry.queue(new MeatgunStaffItem("meatgun_staff", TooltipSupplier.simple(1), new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));

//    public static Item BLASTER = new BlasterItem();
    public static Item HEAVY_CANNON = new HeavyCannonItem();
    public static Item AIRTRUCK_ITEM = new AirtruckItem("airtruck", TooltipSupplier.hidden(2), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static Item ASSAULT_DRILL = new AssaultDrillItem("assault_drill", 1024, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static MeatgunModuleItem PISTOL = ItemRegistry.queue("pistol", new MeatgunModuleItem(MeatgunModules.PISTOL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem BOSHER = ItemRegistry.queue("bosher", new MeatgunModuleItem(MeatgunModules.BOSHER, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem CHUGGER = ItemRegistry.queue("chugger", new MeatgunModuleItem(MeatgunModules.CHUGGER, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem LONG_BOI = ItemRegistry.queue("long_boi", new MeatgunModuleItem(MeatgunModules.LONG_BOI, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem UNDERBARREL = ItemRegistry.queue("underbarrel", new MeatgunModuleItem(MeatgunModules.UNDERBARREL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem BLOODTHROWER = ItemRegistry.queue("bloodthrower", new MeatgunModuleItem(MeatgunModules.BLOODTHROWER, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem GRENADE_LAUNCHER = ItemRegistry.queue("grenade_launcher", new MeatgunModuleItem(MeatgunModules.GRENADE_LAUNCHER, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem DOUBLE_CAROUSEL = ItemRegistry.queue("double_carousel", new MeatgunModuleItem(MeatgunModules.DOUBLE_CAROUSEL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem TRIPLE_CAROUSEL = ItemRegistry.queue("triple_carousel", new MeatgunModuleItem(MeatgunModules.TRIPLE_CAROUSEL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));

    public static MeatgunModuleItem BATTERY = ItemRegistry.queue("battery", new MeatgunModuleItem(MeatgunModules.BATTERY, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem HOMING_BRAIN = ItemRegistry.queue("homing_brain", new MeatgunModuleItem(MeatgunModules.HOMING_BRAIN, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));

    public static MeatgunModuleItem HALBERD = ItemRegistry.queue("halberd", new MeatgunModuleItem(MeatgunModules.HALBERD, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem SHOCK_STAFF = ItemRegistry.queue("shock_staff", new MeatgunModuleItem(MeatgunModules.SHOCK_STAFF, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));

    public static final PlayerImplantItem BLOOD_BULLET_PROVIDER = new PlayerImplantItem("blood_bullet_provider", 1, BloodBulletProviderImplant.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static void init()
    {

    }
}
