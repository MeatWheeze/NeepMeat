package com.neep.neepmeat.init;

import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.item.EnlightenmentFoodItem;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.Path;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.implant.item.ShieldUpgrade;
import com.neep.neepmeat.implant.player.ExtraKneeImplant;
import com.neep.neepmeat.implant.player.ExtraMouthImplant;
import com.neep.neepmeat.implant.player.LungExtensionsImplant;
import com.neep.neepmeat.implant.player.PinealEyeImplant;
import com.neep.neepmeat.item.*;
import com.neep.neepmeat.machine.small_compressor.SmallCompressorMinecart;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.*;

@SuppressWarnings("unused")
@RegisterMe(NeepMeat.NAMESPACE)
public class NMItems
{
    public static final RegistrationContext C = new RegistrationContext(NeepMeat.NAMESPACE);

    public static Item SMALL_COMPRESSOR_MINECART = new BaseMinecartItem(new MeatlibItemSettings().maxCount(3).group(NMItemGroups.GENERAL), SmallCompressorMinecart::new);

    @Path("compound_injector")
    public static Item COMPOUND_INJECTOR = new CompoundInjectorItem(C, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    @Path("sacrificial_dagger")
    public static Item SACRIFICIAL_SCALPEL = new ScalpelItem(C, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item CHEESE_CLEAVER = new CheeseCleaverItem(C, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item SLASHER = new SlasherItem(C, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static Item ANIMAL_HEART = new BaseCraftingItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REANIMATED_HEART = new BaseCraftingItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item COPPER_COIL = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item BIOELECTRIC_ORGAN = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item STATOR = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item FLYWHEEL = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REFRACTORY_BRICK = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item WHISPER_BRASS_INGOT = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item MEAT_STEEL_INGOT = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item MEAT_STEEL_SHOVEL = new ShovelItem(NMToolMaterials.EMBOSSED_MEAT_STEEL, 1.5F, -3.0F, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static Item MEAT_STEEL_PICKAXE = new PickaxeItem(NMToolMaterials.EMBOSSED_MEAT_STEEL, 1, -2.8F, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static Item MEAT_STEEL_AXE = new AxeItem(NMToolMaterials.EMBOSSED_MEAT_STEEL, 6.0F, -3.1F, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static Item MEAT_STEEL_HOE = new MeatSteelHoeItem(NMToolMaterials.EMBOSSED_MEAT_STEEL, 1, 0F, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static Item BLOOD_BUBBLE = new BaseCraftingItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.FOOD).food(NMFoodComponents.BLOOD_BUBBLE));
    public static Item MEAT_STEEL_COMPONENT = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item CONTROL_UNIT = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));

    // MEAT
    public static Item MEAT_SCRAP = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.FOOD).food(NMFoodComponents.MEAT_SCRAP));
    public static Item RAW_MEAT_BRICK = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.FOOD).food(NMFoodComponents.MEAT_BRICK));
    public static Item COOKED_MEAT_BRICK = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.FOOD).food(NMFoodComponents.COOKED_MEAT_BRICK));

//    public static Item MACHINE_FLUID = new BaseCraftingItem("machine_fluid", 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));

    // Organism Parts
//    public static Item DIGESTIVE_SYSTEM = new BaseCraftingItem("digestive_system", 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item INTERFACE_PORTS = new BaseCraftingItem("interface_array", 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item INTERNAL_COMPONENTS = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item CONTRACTILE_ACTUATOR = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item GANGLIAL_CENTRE = new BaseCraftingItem("ganglial_cluster", 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));

    @Path("brain_rough") public static Item ROUGH_BRAIN = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item ENLIGHTENED_BRAIN = new EnlightenmentFoodItem(C, 2, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS).food(NMFoodComponents.ENLIGHTENED_BRAIN));

    public static Item OPEN_EYE = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item PROCESSOR_BOARD = new BaseCraftingItem("processor_board", 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item DIVINE_ORGAN = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));

    @Path("integration_charge_crude")
    public static Item CRUDE_INTEGRATION_CHARGE = new BaseCraftingItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    @Path("integration_charge_adv")
    public static Item ADV_INTEGRATION_CHARGE = new BaseCraftingItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));

    @Path("biomass")
    public static Item ASSORTED_BIOMASS = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));

    public static Item WHISPER_WHEAT = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item WHISPER_FLOUR = new BaseCraftingItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item WHISPER_BREAD = new EnlightenmentFoodItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.FOOD).food(NMFoodComponents.WHISPER_BREAD));

    public static Item PROJECTOR = new ProjectorItem(new MeatlibItemSettings().group(NMItemGroups.GENERAL).maxCount(1));

    public static Item PIPETTE = new PipetteItem(TooltipSupplier.hidden(3), new MeatlibItemSettings().group(NMItemGroups.GENERAL).maxCount(1));

    public static Item PINKDRINK = new PinkdrinkItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.FOOD).food(NMFoodComponents.PINKDRINK));

    public static Item CAN = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.FOOD));
    public static Item CARTON = new BaseCraftingItem(C, 0, new MeatlibItemSettings().group(NMItemGroups.FOOD));
    public static Item MILK_CARTON = new MilkCartonItem(TooltipSupplier.simple(1), new MeatlibItemSettings().group(NMItemGroups.FOOD));
    public static Item MEAT_CARTON = new MeatCartonItem(TooltipSupplier.blank(), new MeatlibItemSettings().group(NMItemGroups.FOOD).food(NMFoodComponents.MEAT_CARTON));

    // Mob cloning
    @Path("essential_saltes")
    public static Item ESSENTIAL_SALTES = new EssentialSaltesItem(C, TooltipSupplier.simple(1), new MeatlibItemSettings().group(NMItemGroups.INGREDIENTS).fireproof());
    public static Item MOB_EGG = new MobEggItem(C, TooltipSupplier.simple(1), new MeatlibItemSettings());

//    public static Item MEAT_STEEL_BOOTS = new MeatSteelArmourItem("meat_steel_boots", ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
//    public static Item MEAT_STEEL_LEGS = new MeatSteelArmourItem("meat_steel_legs", ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
//    public static Item MEAT_STEEL_CHESTPLATE = new MeatSteelArmourItem("meat_steel_chestplate", ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    @Path("goggles")
    public static GogglesItem GOGGLES = new GogglesItem(ArmorMaterials.IRON, new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static PlayerImplantItem PINEAL_EYE = new PlayerImplantItem(C, "pineal_eye", 1, PinealEyeImplant.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static PlayerImplantItem EXTRA_KNEES = new PlayerImplantItem(C, "extra_knees", 1, ExtraKneeImplant.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static PlayerImplantItem EXTRA_MOUTH = new PlayerImplantItem(C, "extra_mouth", 1, ExtraMouthImplant.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static PlayerImplantItem LUNG_EXTENSIONS = new PlayerImplantItem(C, "lung_extensions", 1, LungExtensionsImplant.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    @Path("shield") public static Item SHIELD = new ItemImplantItem(C, "shield", 1, ShieldUpgrade.ID, new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static ChrysalisItem CHRYSALIS = new ChrysalisItem(C ,TooltipSupplier.simple(1), new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static Item TRANSFORMING_TOOL_BASE = new BaseCraftingItem(C, 1, new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static Item NETWORKING_TOOL = new NetworkingToolItem(C, TooltipSupplier.hidden(3), new MeatlibItemSettings().group(NMItemGroups.GENERAL));
    public static Item DOSIMETER = new DosimeterItem(C, TooltipSupplier.hidden(1), new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static Item FARMING_SCUTTER = new FarmingScutterItem(() -> NMEntities.FARMING_SCUTTER, TooltipSupplier.hidden(1), new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static Item ROCK_DRILL = new RockDrillItem(new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    @Path("debug")
    public static Item DEBUG_ITEM = new DebugItem(new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    public static void init()
    {
        registerCompostable(NMBlocks.WHISPER_WHEAT.getSeedsItem(), 0.5f);
        registerCompostable(NMBlocks.FLESH_POTATO.getSeedsItem(), 0.85f);
        registerCompostable(WHISPER_FLOUR, 0.5f);
        registerCompostable(WHISPER_BREAD, 0.85f);
        registerCompostable(MOB_EGG, 1f);
        registerCompostable(ANIMAL_HEART, 0.85f);
        registerCompostable(REANIMATED_HEART, 0.85f);
        registerCompostable(MEAT_SCRAP, 0.5f);
        registerCompostable(RAW_MEAT_BRICK, 1f);
        registerCompostable(COOKED_MEAT_BRICK, 1f);
        registerCompostable(ROUGH_BRAIN, 0.85f);
        registerCompostable(ENLIGHTENED_BRAIN, 0.85f);
        registerCompostable(ASSORTED_BIOMASS, 1f);
    }

    private static void registerCompostable(ItemConvertible itemConvertible, float chance)
    {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(itemConvertible.asItem(), chance);
    }
}
