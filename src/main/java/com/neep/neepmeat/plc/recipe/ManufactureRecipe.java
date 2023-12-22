package com.neep.neepmeat.plc.recipe;

import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;

import java.util.List;

public interface ManufactureRecipe<C> extends MeatRecipe<C>
{
    // TODO: Use generics
    Object getBase();
    List<ManufactureStep<?>> getSteps();
}
