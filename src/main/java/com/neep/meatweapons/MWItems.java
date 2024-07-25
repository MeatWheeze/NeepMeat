package com.neep.meatweapons;

import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.Path;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.meatweapons.implant.BloodBulletProviderImplant;
import com.neep.meatweapons.implant.MagazineOrganImplant;
import com.neep.meatweapons.item.*;
import com.neep.meatweapons.item.meatgun.MeatgunModuleItem;
import com.neep.meatweapons.item.meatgun.MeatgunPistolItem;
import com.neep.meatweapons.item.meatgun.MeatgunStaffItem;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.module.MeatgunModules;
import com.neep.meatweapons.tooltip.MeatgunTooltipBuilder;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.item.PlayerImplantItem;
import net.minecraft.item.Item;

@SuppressWarnings("unused")
@RegisterMe(MeatWeapons.NAMESPACE)
public class MWItems
{
    public static final RegistrationContext C = new RegistrationContext(MeatWeapons.NAMESPACE);

    public static Item SMALL_BALLISTIC_MAGAZINE = new AmmunitionItem(TooltipSupplier.simple(1), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item MEDIUM_BALLISTIC_MAGAZINE = new AmmunitionItem(TooltipSupplier.simple(1), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item LARGE_BALLISTIC_MAGAZINE = new AmmunitionItem(TooltipSupplier.simple(1), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static Item ENGINE = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item IRON_BARREL = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item MEAT_STEEL_BARREL = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    @Path("fusion") public static Item FUSION_CANNON = new FusionCannonItem();
    @Path("hand_cannon") public static Item HAND_CANNON = new HandCannonItem();
    @Path("machine_pistol") public static Item MACHINE_PISTOL = new MachinePistolItem();
    @Path("light_machine_gun") public static Item LMG = new LMGItem();
    @Path("ma75") public static Item MA75 = new MA75Item();
    @Path("heavy_cannon") public static Item HEAVY_CANNON = new HeavyCannonItem();

    public static Item MEATGUN_PISTOL = new MeatgunPistolItem(TooltipSupplier.simple(1), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item MEATGUN_STAFF = new MeatgunStaffItem(TooltipSupplier.simple(1), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

//    public static Item BLASTER = new BlasterItem();
    public static Item AIRTRUCK = new AirtruckItem(TooltipSupplier.hidden(2), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static Item ASSAULT_DRILL = new AssaultDrillItem(1024, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    // Names might change, so I'm explicitly declaring them
    @Path("pistol") public static MeatgunModuleItem PISTOL = new MeatgunModuleItem(MeatgunModules.PISTOL,
            new MeatgunTooltipBuilder().ammoType(AmmunitionType.BALLISTIC).build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("bosher") public static MeatgunModuleItem BOSHER = new MeatgunModuleItem(MeatgunModules.BOSHER,
            new MeatgunTooltipBuilder().ammoType(AmmunitionType.BALLISTIC).desc().build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("chugger") public static MeatgunModuleItem CHUGGER = new MeatgunModuleItem(MeatgunModules.CHUGGER,
            new MeatgunTooltipBuilder().ammoType(AmmunitionType.BALLISTIC).build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("long_boi") public static MeatgunModuleItem LONG_BOI = new MeatgunModuleItem(MeatgunModules.LONG_BOI,
            new MeatgunTooltipBuilder().ammoType(AmmunitionType.BALLISTIC).build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("underbarrel") public static MeatgunModuleItem UNDERBARREL = new MeatgunModuleItem(MeatgunModules.UNDERBARREL,
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("bloodthrower") public static MeatgunModuleItem BLOODTHROWER = new MeatgunModuleItem(MeatgunModules.BLOODTHROWER,
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("grenade_launcher") public static MeatgunModuleItem GRENADE_LAUNCHER = new MeatgunModuleItem(MeatgunModules.GRENADE_LAUNCHER,
            new MeatgunTooltipBuilder().ammoType(AmmunitionType.BALLISTIC).desc().build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("double_carousel") public static MeatgunModuleItem DOUBLE_CAROUSEL = new MeatgunModuleItem(MeatgunModules.DOUBLE_CAROUSEL,
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("triple_carousel") public static MeatgunModuleItem TRIPLE_CAROUSEL = new MeatgunModuleItem(MeatgunModules.TRIPLE_CAROUSEL,
            new MeatgunTooltipBuilder().desc().build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    @Path("battery") public static MeatgunModuleItem BATTERY = new MeatgunModuleItem(MeatgunModules.BATTERY, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("homing_brain") public static MeatgunModuleItem HOMING_BRAIN = new MeatgunModuleItem(MeatgunModules.HOMING_BRAIN,
            new MeatgunTooltipBuilder().desc().build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    @Path("halberd") public static MeatgunModuleItem HALBERD = new MeatgunModuleItem(MeatgunModules.HALBERD,
            new MeatgunTooltipBuilder().ammoType(AmmunitionType.ENERGY).desc().build(),
            new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    @Path("shock_staff") public static MeatgunModuleItem SHOCK_STAFF = new MeatgunModuleItem(MeatgunModules.SHOCK_STAFF, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static final PlayerImplantItem BLOOD_BULLET_PROVIDER = new PlayerImplantItem(C, "blood_bullet_provider", 1, BloodBulletProviderImplant.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static final PlayerImplantItem MAGAZINE_ORGAN = new PlayerImplantItem(C, "magazine_organ", 1, MagazineOrganImplant.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static void init()
    {

    }
}
