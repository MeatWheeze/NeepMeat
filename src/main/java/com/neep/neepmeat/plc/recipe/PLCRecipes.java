package com.neep.neepmeat.plc.recipe;

import com.neep.meatlib.recipe.*;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.init.NMItems;

import java.util.List;
import java.util.Objects;

public class PLCRecipes
{
    public static final MeatRecipeSerialiser<ItemManufactureRecipe> MANUFACTURE_SERIALISER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "manufacture", new ItemManufactureRecipe.Serialiser());
    public static final MeatRecipeType<ItemManufactureRecipe> MANUFACTURE = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "manufacture");
    public static final MeatRecipeSerialiser<EntityImplantRecipe> ENTITY_MANUFACTURE_SERIALISER = RecipeRegistry.registerMeatlibSerializer(NeepMeat.NAMESPACE, "entity_manufacture", new EntityImplantRecipe.Serialiser());
    public static final MeatRecipeType<EntityImplantRecipe> ENTITY_MANUFACTURE = RecipeRegistry.registerMeatlibType(NeepMeat.NAMESPACE, "entity_manufacture");

    public static final ManufactureStep.Provider<?> COMBINE = ManufactureStep.register(CombineStep.ID, ManufactureStep.Provider.of(CombineStep::get, CombineStep::get));
    public static final ManufactureStep.Provider<?> INJECT = ManufactureStep.register(InjectStep.ID, ManufactureStep.Provider.of(InjectStep::new, InjectStep::new));
    public static final ManufactureStep.Provider<?> IMPLANT = ManufactureStep.register(ImplantStep.ID, ManufactureStep.Provider.of(ImplantStep::new, ImplantStep::new));

    public static <B, C, T extends ManufactureRecipe<C>> boolean isValidStep(MeatRecipeType<T> type, Workpiece workpiece, ManufactureStep<?> nextStep, B base)
    {
        // TODO: Somehow remove jank
        if (base == NMItems.TRANSFORMING_TOOL_BASE)
            return true;

        List<ManufactureStep<?>> steps = workpiece.getSteps();
        for (var recipe : MeatlibRecipes.getInstance().getAllOfTypeSafe(type).values())
        {
            if (!Objects.equals(recipe.getBase(), base))
                continue;

            List<ManufactureStep<?>> recipeSteps = recipe.getSteps();
            int nextStepIdx = steps.size();
            if (recipeSteps.size() < steps.size() + 1)
                continue;

            if (ManufactureStep.equals(recipeSteps.get(nextStepIdx), nextStep))
                return true;
        }

        return false;
    }

//    public static <B, C, T extends ManufactureRecipe<C>> boolean isValidStepSuffix(MeatRecipeType<T> type, Workpiece workpiece, ManufactureStep<?> nextStep, B base)
//    {
//        List<ManufactureStep<?>> steps = workpiece.getSteps();
//        for (var recipe : MeatRecipeManager.getInstance().getAllOfType(type).values())
//        {
//            if (!Objects.equals(recipe.getBase(), base))
//                continue;
//
//            List<ManufactureStep<?>> recipeSteps = recipe.getSteps();
//            int nextStepIdx = steps.size();
//            if (recipeSteps.size() < steps.size() + 1)
//                continue;
//
//            if (ManufactureStep.equals(recipeSteps.get(nextStepIdx), nextStep))
//                return true;
//        }
//
//        return false;
//    }

    public static void init()
    {

//        MutateInPlace.ANY.registerFallback((world, pos, state, blockEntity, context) ->
//        {
//
//        });
    }
}
