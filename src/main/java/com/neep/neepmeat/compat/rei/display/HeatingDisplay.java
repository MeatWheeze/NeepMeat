package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.recipe.FluidHeatingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HeatingDisplay extends BasicDisplay
{
    private FluidHeatingRecipe recipe;

    public HeatingDisplay(FluidHeatingRecipe recipe)
    {
        this(
                List.of(
                        SurgeryDisplay.entryFromInput(recipe.getFluidInput())
                        ),
                new ArrayList<>(),
                Optional.empty()
        );
        this.recipe = recipe;
        this.outputs.add(EntryIngredients.of(recipe.getFluidOutput().resource(), recipe.getFluidOutput().minAmount()));
    }

    public HeatingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
    }

    public FluidHeatingRecipe getRecipe()
    {
        return recipe;
    }

    public static Serializer<HeatingDisplay> serializer()
    {
        return Serializer.ofSimple(HeatingDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.HEATING;
    }
}
