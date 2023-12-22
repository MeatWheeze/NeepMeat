package com.neep.neepmeat.plc.recipe;

import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.RecipeRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class PLCRecipes
{
    public static final MeatRecipeSerialiser<ManufactureRecipe> MANUFACTURE_SERIALISER = RecipeRegistry.registerSerializer(NeepMeat.NAMESPACE, "manufacture", new ManufactureRecipe.Serialiser());
    public static final MeatRecipeType<ManufactureRecipe> MIXING = RecipeRegistry.registerSpecialType(NeepMeat.NAMESPACE, "manufacture");


    public static final ManufactureStep.Provider<?> COMBINE = ManufactureStep.register(CombineStep.ID, ManufactureStep.Provider.of(CombineStep::new, CombineStep::new));

    public static boolean isValidStep(Workpiece workpiece, ManufactureStep<?> nextStep)
    {
        List<ManufactureStep<?>> steps = workpiece.getSteps();
        for (var recipe : MeatRecipeManager.getInstance().getAllOfType(MIXING).values())
        {
            List<ManufactureStep<?>> recipeSteps = recipe.getSteps();
            int nextStepIdx = steps.size();
            if (recipeSteps.size() < steps.size() + 1)
                continue;

            if (ManufactureStep.equals(recipeSteps.get(nextStepIdx), nextStep))
                return true;
        }

        return false;
    }

    public static void init()
    {
        ItemTooltipCallback.EVENT.register((stack, context, lines) ->
        {
            if (ItemWorkpiece.has(stack))
            {
                NMComponents.WORKPIECE.maybeGet(stack).ifPresent(workpiece ->
                {
                    var steps = workpiece.getSteps();
                    if (!steps.isEmpty())
                    {
                        lines.add(Text.translatable("message.neepmeat.workpiece.title").formatted(Formatting.GOLD, Formatting.BOLD));
                        for (var entry : steps)
                        {
                            entry.appendText(lines);
                        }
                    }
                });
            }
        });
    }
}
