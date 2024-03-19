package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.processing.FluidEnegyRegistry;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.fluid.FluidFactory;
import com.neep.neepmeat.fluid.MeatFluidFactory;
import com.neep.neepmeat.fluid.ore_fat.OreFatFluidFactory;
import com.neep.neepmeat.item.MeatCartonStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.block.Block;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

@SuppressWarnings("UnstableApiUsage")
public class NMFluids
{
    public static FlowableFluid FLOWING_BLOOD;
    public static FlowableFluid STILL_BLOOD;
    public static Item BLOOD_BUCKET;
    public static Block BLOOD;
    public static FluidFactory BLOOD_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "blood", false, 5, 1);

//    public static FlowableFluid FLOWING_ENRICHED_BLOOD;
//    public static FlowableFluid STILL_ENRICHED_BLOOD;
//    public static Item ENRICHED_BLOOD_BUCKET;
//    public static Block ENRICHED_BLOOD;
//    public static FluidFactory ENRICHED_BLOOD_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "enriched_blood", false, 10, 6, 1);

    public static FlowableFluid FLOWING_WORK_FLUID;
    public static FlowableFluid STILL_WORK_FLUID;
    public static Item WORK_FLUID_BUCKET;
    public static Block WORK_FLUID;
    public static FluidFactory WORK_FLUID_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "work_fluid", false, 6, 1);

    public static FlowableFluid FLOWING_CHARGED_WORK_FLUID;
    public static FlowableFluid STILL_CHARGED_WORK_FLUID;
    public static Item CHARGED_WORK_FLUID_BUCKET;
    public static Block CHARGED_WORK_FLUID;
    public static FluidFactory CHARGED_WORK_FLUID_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "charged_work_fluid", false, 6, 1);

    public static FlowableFluid FLOWING_PATINA_TREATMENT;
    public static FlowableFluid STILL_PATINA_TREATMENT;
    public static Item PATINA_TREATMENT_BUCKET;
    public static Block PATINA_TREATMENT;
    public static FluidFactory PATINA = new FluidFactory(NeepMeat.NAMESPACE, "patina_treatment", false, 5, 2);

    public static FlowableFluid FLOWING_ETHEREAL_FUEL;
    public static FlowableFluid STILL_ETHEREAL_FUEL;
    public static Item ETHEREAL_FUEL_BUCKET;
    public static Block ETHEREAL_FUEL;
    public static FluidFactory ETHEREAL_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "ethereal_fuel", false, 5, 2);

    public static FlowableFluid FLOWING_ELDRITCH_ENZYMES;
    public static FlowableFluid STILL_ELDRITCH_ENZYMES;
    public static Item ELDRITCH_ENZYMES_BUCKET;
    public static Block ELDRITCH_ENZYMES;
    public static FluidFactory ENZYMES_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "eldritch_enzymes", false, 5, 2);

    public static FlowableFluid FLOWING_DIRTY_ORE_FAT;
    public static FlowableFluid STILL_DIRTY_ORE_FAT;
    public static Block DIRTY_ORE_FAT;
    public static OreFatFluidFactory DIRTY_ORE_FAT_FACTORY = new OreFatFluidFactory(NeepMeat.NAMESPACE, "ore_fat", false, 5, 2);

    public static FlowableFluid FLOWING_CLEAN_ORE_FAT;
    public static FlowableFluid STILL_CLEAN_ORE_FAT;
    public static Block CLEAN_ORE_FAT;
    public static OreFatFluidFactory CLEAN_ORE_FAT_FACTORY = new OreFatFluidFactory(NeepMeat.NAMESPACE, "clean_ore_fat", false, 5, 2);

    public static FlowableFluid FLOWING_C_MEAT;
    public static FlowableFluid STILL_C_MEAT;
    public static Item C_MEAT_BUCKET;
    public static Block C_MEAT;
    public static FluidFactory C_MEAT_FACTORY = new MeatFluidFactory(NeepMeat.NAMESPACE, "coarse_meat", false, 5, 2);

    public static FlowableFluid FLOWING_MEAT;
    public static FlowableFluid STILL_MEAT;
    public static Item MEAT_BUCKET;
    public static Block MEAT;
    public static FluidFactory MEAT_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "meat", false, 5, 2);

    public static FlowableFluid FLOWING_TISSUE_SLURRY;
    public static FlowableFluid STILL_TISSUE_SLURRY;
    public static Item TISSUE_SLURRY_BUCKET;
    public static Block TISSUE_SLURRY;
    public static FluidFactory TISSUE_SLURRY_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "tissue_slurry", false, 5, 2);

    public static FlowableFluid FLOWING_MILK;
    public static FlowableFluid STILL_MILK;
    public static Block MILK;
    public static FluidFactory MILK_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "milk", false, 5, 1).withBucketItem(Items.MILK_BUCKET);

    public static FlowableFluid FLOWING_P_MILK;
    public static FlowableFluid STILL_PASTEURISED_MILK;
    public static Item P_MILK_BUCKET;
    public static Block P_MILK;
    public static FluidFactory P_MILK_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "pasteurised_milk", false, 5, 1);

    public static FlowableFluid FLOWING_FEED;
    public static FlowableFluid STILL_FEED;
    public static Item FEED_BUCKET;
    public static Block FEED;
    public static FluidFactory FEED_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "animal_feed", false, 5, 2);

    public static FlowableFluid FLOWING_PINKDRINK;
    public static FlowableFluid STILL_PINKDRINK;
    public static Item PINKDRINK_BUCKET;
    public static Block PINKDRINK;
    public static FluidFactory PINKDRINK_FACTORY = new FluidFactory(NeepMeat.NAMESPACE, "pinkdrink", false, 5, 2);

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

        STILL_C_MEAT = C_MEAT_FACTORY.registerStill();
        FLOWING_C_MEAT = C_MEAT_FACTORY.registerFlowing();
        C_MEAT_BUCKET = C_MEAT_FACTORY.registerItem();
        C_MEAT = C_MEAT_FACTORY.registerBlock();

        STILL_MEAT = MEAT_FACTORY.registerStill();
        FLOWING_MEAT = MEAT_FACTORY.registerFlowing();
        MEAT_BUCKET = MEAT_FACTORY.registerItem();
        MEAT = MEAT_FACTORY.registerBlock();

        STILL_TISSUE_SLURRY = TISSUE_SLURRY_FACTORY.registerStill();
        FLOWING_TISSUE_SLURRY = TISSUE_SLURRY_FACTORY.registerFlowing();
        TISSUE_SLURRY_BUCKET = TISSUE_SLURRY_FACTORY.registerItem();
        TISSUE_SLURRY = TISSUE_SLURRY_FACTORY.registerBlock();

        STILL_MILK = MILK_FACTORY.registerStill();
        FLOWING_MILK = MILK_FACTORY.registerFlowing();
        MILK = MILK_FACTORY.registerBlock();

        STILL_PASTEURISED_MILK = P_MILK_FACTORY.registerStill();
        FLOWING_P_MILK = P_MILK_FACTORY.registerFlowing();
        P_MILK_BUCKET = P_MILK_FACTORY.registerItem();
        P_MILK = P_MILK_FACTORY.registerBlock();

        STILL_FEED = FEED_FACTORY.registerStill();
        FLOWING_FEED = FEED_FACTORY.registerFlowing();
        FEED_BUCKET = FEED_FACTORY.registerItem();
        FEED = FEED_FACTORY.registerBlock();

        STILL_PINKDRINK = PINKDRINK_FACTORY.registerStill();
        FLOWING_PINKDRINK = PINKDRINK_FACTORY.registerFlowing();
        PINKDRINK_BUCKET = PINKDRINK_FACTORY.registerItem();
        PINKDRINK = PINKDRINK_FACTORY.registerBlock();

        FluidEnegyRegistry.getInstance().register(STILL_ETHEREAL_FUEL, 2 * PowerUtils.DROPLET_POWER, true, null);
//        FluidEnegyRegistry.getInstance().register(Fluids.WATER, 1 * PowerUtils.DROPLET_POWER, false, null);
//        FluidEnegyRegistry.getInstance().register(STILL_CHARGED_WORK_FLUID, 14 * PowerUtils.DROPLET_POWER, true, STILL_WORK_FLUID);

        CHARGED = FluidVariant.of(STILL_CHARGED_WORK_FLUID);
        UNCHARGED = FluidVariant.of(STILL_WORK_FLUID);

        FluidStorage.combinedItemApiProvider(NMItems.PINKDRINK).register(context ->
                new FullItemFluidStorage(context, Items.GLASS_BOTTLE, FluidVariant.of(NMFluids.STILL_PINKDRINK), FluidConstants.BOTTLE));
        FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(context ->
                new EmptyItemFluidStorage(context, NMItems.PINKDRINK, NMFluids.STILL_PINKDRINK, FluidConstants.BOTTLE));

        FluidStorage.combinedItemApiProvider(NMItems.MILK_CARTON).register(context ->
                new FullItemFluidStorage(context, NMItems.CARTON, FluidVariant.of(NMFluids.STILL_PASTEURISED_MILK), FluidConstants.BOTTLE));
        FluidStorage.combinedItemApiProvider(NMItems.CARTON).register(context ->
                new EmptyItemFluidStorage(context, NMItems.MILK_CARTON, NMFluids.STILL_PASTEURISED_MILK, FluidConstants.BOTTLE));

//        FluidStorage.combinedItemApiProvider(NMItems.MILK_CARTON).register(context ->
//                new FullItemFluidStorage(context, NMItems.CARTON, FluidVariant.of(NMFluids.STILL_P_MILK), FluidConstants.BOTTLE));
        FluidStorage.combinedItemApiProvider(NMItems.CARTON).register(context ->
                new MeatCartonStorage(context, NMItems.CARTON, NMItems.MEAT_CARTON, NMFluids.STILL_C_MEAT, FluidConstants.INGOT));

        FluidStorage.combinedItemApiProvider(Items.MILK_BUCKET).register(context ->
                new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(NMFluids.STILL_MILK), FluidConstants.BUCKET));
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
                new EmptyItemFluidStorage(context, Items.MILK_BUCKET, NMFluids.STILL_MILK, FluidConstants.BUCKET));
    }
}