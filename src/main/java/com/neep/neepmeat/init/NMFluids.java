package com.neep.neepmeat.init;

import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.fluid.*;
import com.neep.neepmeat.fluid.ore_fat.OreFatFluidFactory;
import com.neep.neepmeat.item.BaseBucketItem;
import com.neep.neepmeat.machine.FluidFuelRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMFluids
{
    public static FlowableFluid FLOWING_BLOOD;
    public static FlowableFluid STILL_BLOOD;
    public static Item BLOOD_BUCKET;
    public static Block BLOOD;
    public static FluidFactory BLOOD_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "blood", false, 10, 5, 2);

//    public static FlowableFluid FLOWING_ENRICHED_BLOOD;
//    public static FlowableFluid STILL_ENRICHED_BLOOD;
//    public static Item ENRICHED_BLOOD_BUCKET;
//    public static Block ENRICHED_BLOOD;
//    public static FluidFactory ENRICHED_BLOOD_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "enriched_blood", false, 10, 6, 1);

    public static FlowableFluid FLOWING_WORK_FLUID;
    public static FlowableFluid STILL_WORK_FLUID;
    public static Item WORK_FLUID_BUCKET;
    public static Block WORK_FLUID;
    public static FluidFactory WORK_FLUID_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "work_fluid", false, 10, 6, 1);

    public static FlowableFluid FLOWING_CHARGED_WORK_FLUID;
    public static FlowableFluid STILL_CHARGED_WORK_FLUID;
    public static Item CHARGED_WORK_FLUID_BUCKET;
    public static Block CHARGED_WORK_FLUID;
    public static FluidFactory CHARGED_WORK_FLUID_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "charged_work_fluid", false, 10, 6, 1);

    public static FlowableFluid FLOWING_PATINA_TREATMENT;
    public static FlowableFluid STILL_PATINA_TREATMENT;
    public static Item PATINA_TREATMENT_BUCKET;
    public static Block PATINA_TREATMENT;
    public static FluidFactory PATINA = new FluidFactory(NeepMeat.NAMESPACE, "patina_treatment", false, 10, 5, 2);

    public static FlowableFluid FLOWING_ETHEREAL_FUEL;
    public static FlowableFluid STILL_ETHEREAL_FUEL;
    public static Item ETHEREAL_FUEL_BUCKET;
    public static Block ETHEREAL_FUEL;
    public static FluidFactory ETHEREAL_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "ethereal_fuel", false, 10, 5, 2);

    public static FlowableFluid FLOWING_ELDRITCH_ENZYMES;
    public static FlowableFluid STILL_ELDRITCH_ENZYMES;
    public static Item ELDRITCH_ENZYMES_BUCKET;
    public static Block ELDRITCH_ENZYMES;
    public static FluidFactory ENZYMES_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "eldritch_enzymes", false, 10, 5, 2);

    public static FlowableFluid FLOWING_DIRTY_ORE_FAT;
    public static FlowableFluid STILL_DIRTY_ORE_FAT;
    public static Block DIRTY_ORE_FAT;
    public static OreFatFluidFactory DIRTY_ORE_FAT_FACTORY = new OreFatFluidFactory(NeepMeat.NAMESPACE, "ore_fat", false, 10, 5, 2);

    public static FlowableFluid FLOWING_CLEAN_ORE_FAT;
    public static FlowableFluid STILL_CLEAN_ORE_FAT;
    public static Block CLEAN_ORE_FAT;
    public static OreFatFluidFactory CLEAN_ORE_FAT_FACTORY = new OreFatFluidFactory(NeepMeat.NAMESPACE, "clean_ore_fat", false, 10, 5, 2);

    public static FluidVariant CHARGED;
    public static FluidVariant UNCHARGED;

    public static void initialise()
    {
        STILL_BLOOD = BLOOD_FACTORY.registerStill();
        FLOWING_BLOOD = BLOOD_FACTORY.registerFlowing();
        BLOOD_BUCKET = BLOOD_FACTORY.registerItem();
        BLOOD = BLOOD_FACTORY.registerBlock();

//        STILL_ENRICHED_BLOOD = ENRICHED_BLOOD_FACTORY.registerStill();
//        FLOWING_ENRICHED_BLOOD = ENRICHED_BLOOD_FACTORY.registerFlowing();
//        ENRICHED_BLOOD_BUCKET = ENRICHED_BLOOD_FACTORY.registerItem();
//        ENRICHED_BLOOD = ENRICHED_BLOOD_FACTORY.registerBlock();

        STILL_WORK_FLUID = WORK_FLUID_FACTORY.registerStill();
        FLOWING_WORK_FLUID = WORK_FLUID_FACTORY.registerFlowing();
        WORK_FLUID_BUCKET = WORK_FLUID_FACTORY.registerItem();
        WORK_FLUID = WORK_FLUID_FACTORY.registerBlock();

        STILL_CHARGED_WORK_FLUID = CHARGED_WORK_FLUID_FACTORY.registerStill();
        FLOWING_CHARGED_WORK_FLUID = CHARGED_WORK_FLUID_FACTORY.registerFlowing();
        CHARGED_WORK_FLUID_BUCKET = CHARGED_WORK_FLUID_FACTORY.registerItem();
        CHARGED_WORK_FLUID = CHARGED_WORK_FLUID_FACTORY.registerBlock();

        STILL_PATINA_TREATMENT = PATINA.registerStill();
        FLOWING_PATINA_TREATMENT = PATINA.registerFlowing();
        PATINA_TREATMENT_BUCKET = PATINA.registerItem();
        PATINA_TREATMENT = PATINA.registerBlock();

        STILL_ETHEREAL_FUEL = ETHEREAL_FACTORY.registerStill();
        FLOWING_ETHEREAL_FUEL = ETHEREAL_FACTORY.registerFlowing();
        ETHEREAL_FUEL_BUCKET = ETHEREAL_FACTORY.registerItem();
        ETHEREAL_FUEL = ETHEREAL_FACTORY.registerBlock();

        STILL_ELDRITCH_ENZYMES = ENZYMES_FACTORY.registerStill();
        FLOWING_ELDRITCH_ENZYMES = ENZYMES_FACTORY.registerFlowing();
        ELDRITCH_ENZYMES_BUCKET = ENZYMES_FACTORY.registerItem();
        ELDRITCH_ENZYMES = ENZYMES_FACTORY.registerBlock();

        STILL_DIRTY_ORE_FAT = DIRTY_ORE_FAT_FACTORY.registerStill();
        FLOWING_DIRTY_ORE_FAT = DIRTY_ORE_FAT_FACTORY.registerFlowing();
        DIRTY_ORE_FAT = DIRTY_ORE_FAT_FACTORY.registerBlock();

        STILL_CLEAN_ORE_FAT = CLEAN_ORE_FAT_FACTORY.registerStill();
        FLOWING_CLEAN_ORE_FAT = CLEAN_ORE_FAT_FACTORY.registerFlowing();
        CLEAN_ORE_FAT = CLEAN_ORE_FAT_FACTORY.registerBlock();

        FluidFuelRegistry.getInstance().register(STILL_ETHEREAL_FUEL, 3, true, null);
        FluidFuelRegistry.getInstance().register(Fluids.WATER, 2, false, null);
        FluidFuelRegistry.getInstance().register(STILL_CHARGED_WORK_FLUID, 5, true, STILL_WORK_FLUID);

        CHARGED = FluidVariant.of(STILL_CHARGED_WORK_FLUID);
        UNCHARGED = FluidVariant.of(STILL_WORK_FLUID);
    }
}