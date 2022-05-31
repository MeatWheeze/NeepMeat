package com.neep.neepmeat.init;

import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.fluid.BloodFluid;
import com.neep.neepmeat.fluid.ChargedWorkFluid;
import com.neep.neepmeat.fluid.EnrichedBloodFluid;
import com.neep.neepmeat.fluid.WorkFluid;
import com.neep.neepmeat.item.BaseBucketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
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

        CHARGED = FluidVariant.of(STILL_CHARGED_WORK_FLUID);
        UNCHARGED = FluidVariant.of(STILL_WORK_FLUID);
    }
}