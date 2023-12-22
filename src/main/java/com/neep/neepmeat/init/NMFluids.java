package com.neep.neepmeat.init;

import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.fluid.*;
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

    public static FlowableFluid FLOWING_ENRICHED_BLOOD;
    public static FlowableFluid STILL_ENRICHED_BLOOD;
    public static Item ENRICHED_BLOOD_BUCKET;
    public static Block ENRICHED_BLOOD;

    public static FlowableFluid FLOWING_WORK_FLUID;
    public static FlowableFluid STILL_WORK_FLUID;
    public static Item WORK_FLUID_BUCKET;
    public static Block WORK_FLUID;

    public static FlowableFluid FLOWING_CHARGED_WORK_FLUID;
    public static FlowableFluid STILL_CHARGED_WORK_FLUID;
    public static Item CHARGED_WORK_FLUID_BUCKET;
    public static Block CHARGED_WORK_FLUID;


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

    public static FluidVariant CHARGED;
    public static FluidVariant UNCHARGED;

    public static void initialise()
    {
        STILL_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "blood"), new BloodFluid.Still());
        FLOWING_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "flowing_blood"), new BloodFluid.Flowing());
        BLOOD_BUCKET = new BaseBucketItem(NeepMeat.NAMESPACE, "blood_bucket", STILL_BLOOD, new FabricItemSettings().group(NMItemGroups.GENERAL).maxCount(1));
        BLOOD = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "blood"), new FluidBlock(NMFluids.STILL_BLOOD, FabricBlockSettings.copy(Blocks.WATER)){});

        STILL_ENRICHED_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "enriched_blood"), new EnrichedBloodFluid.Still());
        FLOWING_ENRICHED_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "enriched_flowing_blood"), new EnrichedBloodFluid.Flowing());
        ENRICHED_BLOOD_BUCKET = new BaseBucketItem(NeepMeat.NAMESPACE, "enriched_blood_bucket", STILL_ENRICHED_BLOOD, new FabricItemSettings().group(NMItemGroups.GENERAL).maxCount(1));
        ENRICHED_BLOOD = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "enriched_blood"), new FluidBlock(NMFluids.STILL_ENRICHED_BLOOD, FabricBlockSettings.copy(Blocks.WATER)){});

        STILL_WORK_FLUID = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "work_fluid"), new WorkFluid.Still());
        FLOWING_WORK_FLUID = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "flowing_work_fluid"), new WorkFluid.Flowing());
        WORK_FLUID_BUCKET = new BaseBucketItem(NeepMeat.NAMESPACE, "work_fluid_bucket", STILL_WORK_FLUID, new FabricItemSettings().group(NMItemGroups.GENERAL).maxCount(1));
        WORK_FLUID = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "work_fluid"), new FluidBlock(NMFluids.STILL_WORK_FLUID, FabricBlockSettings.copy(Blocks.WATER)){});

        STILL_CHARGED_WORK_FLUID = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "charged_work_fluid"), new ChargedWorkFluid.Still());
        FLOWING_CHARGED_WORK_FLUID = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "flowing_charged_work_fluid"), new ChargedWorkFluid.Flowing());
        CHARGED_WORK_FLUID_BUCKET = new BaseBucketItem(NeepMeat.NAMESPACE, "charged_work_fluid_bucket", STILL_CHARGED_WORK_FLUID, new FabricItemSettings().group(NMItemGroups.GENERAL).maxCount(1));
        CHARGED_WORK_FLUID = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "charged_work_fluid"), new FluidBlock(NMFluids.STILL_CHARGED_WORK_FLUID, FabricBlockSettings.copy(Blocks.WATER)){});

//        STILL_PATINA_TREATMENT = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "patina_treatment"), new ChargedWorkFluid.Still());
//        FLOWING_PATINA_TREATMENT = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "flowing_patina_treatment"), new ChargedWorkFluid.Flowing());
//        PATINA_TREATMENT_BUCKET = new BaseBucketItem(NeepMeat.NAMESPACE, "patina_treatment_bucket", STILL_CHARGED_WORK_FLUID, new FabricItemSettings().group(NMItemGroups.GENERAL).maxCount(1));
//        PATINA_TREATMENT = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "patina_treatment"), new FluidBlock(NMFluids.STILL_CHARGED_WORK_FLUID, FabricBlockSettings.copy(Blocks.WATER)){});

        STILL_PATINA_TREATMENT = PATINA.registerStill();
        FLOWING_PATINA_TREATMENT = PATINA.registerFlowing();
        PATINA_TREATMENT_BUCKET = PATINA.registerItem();
        PATINA_TREATMENT = PATINA.registerBlock();

        STILL_ETHEREAL_FUEL = ETHEREAL_FACTORY.registerStill();
        FLOWING_ETHEREAL_FUEL = ETHEREAL_FACTORY.registerFlowing();
        ETHEREAL_FUEL_BUCKET = ETHEREAL_FACTORY.registerItem();
        ETHEREAL_FUEL = ETHEREAL_FACTORY.registerBlock();

        FluidFuelRegistry.getInstance().register(STILL_ETHEREAL_FUEL, 2, true, null);
        FluidFuelRegistry.getInstance().register(Fluids.WATER, 1, false, null);

        CHARGED = FluidVariant.of(STILL_CHARGED_WORK_FLUID);
        UNCHARGED = FluidVariant.of(STILL_WORK_FLUID);
    }
}