package com.neep.neepmeat.init;

import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.RecipeRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
import com.neep.neepmeat.recipe.*;
import com.neep.neepmeat.recipe.surgery.GeneralSurgeryRecipe;
import com.neep.neepmeat.recipe.surgery.TransformingToolRecipe;
import com.neep.neepmeat.recipe.surgery.ImplantInstallRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class NMrecipeTypes
{
    public static void init()
    {

    }

    public static final RecipeSerializer<MixingRecipe> MIXING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "mixing", new MixingRecipe.MixerSerializer(MixingRecipe::new, 60));
    public static final RecipeType<MixingRecipe> MIXING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "mixing");

    public static final MeatRecipeSerialiser<GrindingRecipe> GRINDING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "grinding", new GrindingRecipe.Serializer(GrindingRecipe::new, 60));
    public static final MeatRecipeType<GrindingRecipe> GRINDING = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "grinding");

    public static final MeatRecipeSerialiser<AlloyKilnRecipe> ALLOY_KILN_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "alloy_smelting", new AlloyKilnRecipe.Serializer(AlloyKilnRecipe::new, 60));
    public static final MeatRecipeType<AlloyKilnRecipe> ALLOY_SMELTING = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "alloy_smelting");

    public static final RecipeSerializer<RenderingRecipe> RENDERING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "rendering", new RenderingRecipe.Serializer(RenderingRecipe::new));
    public static final RecipeType<RenderingRecipe> RENDERING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "rendering");

    public static final RecipeSerializer<OreFatRenderingRecipe> ORE_FAT_RENDERING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "ore_fat_rendering", new OreFatRenderingRecipe.Serializer(OreFatRenderingRecipe::new));
    public static final RecipeType<OreFatRenderingRecipe> ORE_FAT_RENDERING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "ore_fat_rendering");

    public static final RecipeSerializer<FatPressingRecipe> FAT_PRESSING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "fat_pressing", new FatPressingRecipe.Serializer(FatPressingRecipe::new));
    public static final RecipeType<FatPressingRecipe> FAT_PRESSING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "fat_pressing");

    public static final RecipeSerializer<PressingRecipe> PRESSING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "pressing", new PressingRecipe.Serializer(PressingRecipe::new));
    public static final RecipeType<PressingRecipe> PRESSING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "pressing");

    public static final RecipeSerializer<MobSqueezingRecipe> MOB_SQUEEZING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "mob_squeezing", new MobSqueezingRecipe.Serializer(MobSqueezingRecipe::new));
    public static final RecipeType<MobSqueezingRecipe> MOB_SQUEEZING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "mob_squeezing");

    public static final RecipeSerializer<EnlighteningRecipe> ENLIGHTENING_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "enlightening", new EnlighteningRecipe.Serializer(EnlighteningRecipe::new));
    public static final RecipeType<EnlighteningRecipe> ENLIGHTENING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "enlightening");

    public static final MeatRecipeSerialiser<TrommelRecipe> TROMMEL_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "trommel", new TrommelRecipe.Serializer(TrommelRecipe::new));
    public static final MeatRecipeType<TrommelRecipe> TROMMEL = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "trommel");

    public static final MeatRecipeSerialiser<GeneralSurgeryRecipe> SURGERY_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "surgery", new GeneralSurgeryRecipe.Serializer());
    public static final MeatRecipeType<GeneralSurgeryRecipe> SURGERY = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "surgery");

    public static final MeatRecipeSerialiser<TransformingToolRecipe> TRANSFORMING_TOOL_SERIALISER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "transforming_tool", new TransformingToolRecipe.Serialiser());
    public static final MeatRecipeType<TransformingToolRecipe> TRANSFORMING_TOOL = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "transforming_tool");

    public static final MeatRecipeSerialiser<ImplantInstallRecipe> IMPLANT_INSTALL_SERIALIZER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "implant_install", new ImplantInstallRecipe.Serializer());
    public static final MeatRecipeType<ImplantInstallRecipe> IMPLANT_INSTALL = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "implant_install");

    public static final MeatRecipeSerialiser<FluidHeatingRecipe> HEATING_SERIALISER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "heating", new FluidHeatingRecipe.Serializer(FluidHeatingRecipe::new, 60));
    public static final MeatRecipeType<FluidHeatingRecipe> HEATING = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "heating");

    public static final MeatRecipeSerialiser<VivisectionRecipe> VIVISECTION_SERIALISER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "vivisection", new VivisectionRecipe.Serializer());
    public static final MeatRecipeType<VivisectionRecipe> VIVISECTION = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "vivisection");
}