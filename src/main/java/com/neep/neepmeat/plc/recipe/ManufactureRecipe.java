package com.neep.neepmeat.plc.recipe;

import com.neep.meatlib.recipe.MeatlibRecipe;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;

import java.util.List;

public interface ManufactureRecipe<C, B> extends MeatlibRecipe<C>
{
    // TODO: Use generics
    B getBase();
    List<ManufactureStep<?>> getSteps();
}
