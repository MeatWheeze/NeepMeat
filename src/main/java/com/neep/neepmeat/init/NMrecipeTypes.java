package com.neep.neepmeat.init;

import com.neep.meatlib.recipe.RecipeRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
import com.neep.neepmeat.recipe.GrindingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class NMrecipeTypes
{
    public static void init()
    {

    }

    public static final RecipeSerializer<MixingRecipe> MIXING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "mixing", new MixingRecipe.MixerSerializer(MixingRecipe::new, 60));
    public static final RecipeType<MixingRecipe> MIXING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "mixing");

    public static final RecipeSerializer<GrindingRecipe> GRINDING_SERIALIZER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "grinding", new GrindingRecipe.Serializer(GrindingRecipe::new, 60));
    public static final RecipeType<GrindingRecipe> GRINDING = RecipeRegistry.registerType(NeepMeat.NAMESPACE, "grinding");

}
