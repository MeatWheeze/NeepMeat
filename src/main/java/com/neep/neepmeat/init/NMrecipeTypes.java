package com.neep.neepmeat.init;

import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.RecipeRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
import com.neep.neepmeat.recipe.*;
import com.neep.neepmeat.recipe.surgery.SurgeryRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class NMrecipeTypes
{
    public static void init()
    {

    }

    public static final RecipeSerializer<MixingRecipe> MIXING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "mixing", new MixingRecipe.MixerSerializer(MixingRecipe::new, 60));
    public static final RecipeType<MixingRecipe> MIXING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "mixing");

    public static final MeatRecipeSerialiser<GrindingRecipe> GRINDING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "grinding", new GrindingRecipe.Serializer(GrindingRecipe::new, 60));
    public static final MeatRecipeType<GrindingRecipe> GRINDING = RecipeRegistry.registerSpecialType(NeepMeat.NAMESPACE, "grinding");

    public static final RecipeSerializer<AlloyKilnRecipe> ALLOY_KILN_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "alloy_smelting", new AlloyKilnRecipe.Serializer(AlloyKilnRecipe::new, 60));
    public static final RecipeType<AlloyKilnRecipe> ALLOY_SMELTING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "alloy_smelting");

    public static final RecipeSerializer<RenderingRecipe> RENDERING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "rendering", new RenderingRecipe.Serializer(RenderingRecipe::new));
    public static final RecipeType<RenderingRecipe> RENDERING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "rendering");

    public static final RecipeSerializer<OreFatRenderingRecipe> ORE_FAT_RENDERING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "ore_fat_rendering", new OreFatRenderingRecipe.Serializer(OreFatRenderingRecipe::new));
    public static final RecipeType<OreFatRenderingRecipe> ORE_FAT_RENDERING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "ore_fat_rendering");

    public static final RecipeSerializer<FatPressingRecipe> FAT_PRESSING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "fat_pressing", new FatPressingRecipe.Serializer(FatPressingRecipe::new));
    public static final RecipeType<FatPressingRecipe> FAT_PRESSING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "fat_pressing");

    public static final RecipeSerializer<PressingRecipe> PRESSING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "pressing", new PressingRecipe.Serializer(PressingRecipe::new));
    public static final RecipeType<PressingRecipe> PRESSING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "pressing");

    public static final RecipeSerializer<MobSqueezingRecipe> MOB_SQUEEZING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "mob_squeezing", new MobSqueezingRecipe.Serializer(MobSqueezingRecipe::new));
    public static final RecipeType<MobSqueezingRecipe> MOB_SQUEEZING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "mob_squeezing");

    public static final RecipeSerializer<EnlighteningRecipe> ENLIGHTENING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "enlightening", new EnlighteningRecipe.Serializer(EnlighteningRecipe::new));
    public static final RecipeType<EnlighteningRecipe> ENLIGHTENING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "enlightening");

    public static final RecipeSerializer<TrommelRecipe> TROMMEL_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "trommel", new TrommelRecipe.Serializer(TrommelRecipe::new));
    public static final RecipeType<TrommelRecipe> TROMMEL = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "trommel");

    public static final MeatRecipeSerialiser<SurgeryRecipe> SURGERY_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "surgery", new SurgeryRecipe.Serializer(SurgeryRecipe::new));
    public static final MeatRecipeType<SurgeryRecipe> SURGERY = RecipeRegistry.registerSpecialType(NeepMeat.NAMESPACE, "surgery");

}
