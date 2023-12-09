package com.neep.neepmeat.plc.recipe;

import com.neep.meatlib.recipe.*;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;

public class PLCRecipes
{
    public static final MeatRecipeSerialiser<ItemManufactureRecipe> MANUFACTURE_SERIALISER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "manufacture", new ItemManufactureRecipe.Serialiser());
    public static final MeatRecipeType<ItemManufactureRecipe> MANUFACTURE = RecipeRegistry.registerSpecialType(NeepMeat.NAMESPACE, "manufacture");
    public static final MeatRecipeSerialiser<EntityManufactureRecipe> ENTITY_MANUFACTURE_SERIALISER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "entity_manufacture", new EntityManufactureRecipe.Serialiser());
    public static final MeatRecipeType<EntityManufactureRecipe> ENTITY_MANUFACTURE = RecipeRegistry.registerSpecialType(NeepMeat.NAMESPACE, "entity_manufacture");

    public static final ManufactureStep.Provider<?> COMBINE = ManufactureStep.register(CombineStep.ID, ManufactureStep.Provider.of(CombineStep::new, CombineStep::new));
    public static final ManufactureStep.Provider<?> INJECT = ManufactureStep.register(InjectStep.ID, ManufactureStep.Provider.of(InjectStep::new, InjectStep::new));
    public static final ManufactureStep.Provider<?> IMPLANT = ManufactureStep.register(ImplantStep.ID, ManufactureStep.Provider.of(ImplantStep::new, ImplantStep::new));

    public static <B, C, T extends ManufactureRecipe<C>> boolean isValidStep(MeatRecipeType<T> type, Workpiece workpiece, ManufactureStep<?> nextStep, B base)
    {
        List<ManufactureStep<?>> steps = workpiece.getSteps();
        for (var recipe : MeatRecipeManager.getInstance().getAllOfType(type).values())
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
