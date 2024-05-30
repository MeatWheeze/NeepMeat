package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.recipe.ImplantStep;
import com.neep.neepmeat.plc.recipe.InjectStep;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.List;

public abstract class ManufactureDisplay<T> implements Display
{
    private final T base;
    protected final List<ManufactureStep<?>> steps;

    public ManufactureDisplay(T base, List<ManufactureStep<?>> steps)
    {
        this.base = base;
        this.steps = steps;
    }

    public T getBase()
    {
        return base;
    }

    protected static void appendStepIngredients(List<ManufactureStep<?>> steps, List<EntryIngredient> ingredients)
    {
        for (var step : steps)
        {
            if (step instanceof CombineStep combineStep)
            {
                ingredients.add(EntryIngredients.of(combineStep.getItem()));
            }
            else if (step instanceof InjectStep injectStep)
            {
                ingredients.add(EntryIngredients.of(injectStep.getFluid()));
            }
            else if (step instanceof ImplantStep implantStep)
            {
                ingredients.add(EntryIngredients.of(implantStep.getItem()));
            }
        }
    }

    public List<ManufactureStep<?>> getSteps()
    {
        return steps;
    }
}
