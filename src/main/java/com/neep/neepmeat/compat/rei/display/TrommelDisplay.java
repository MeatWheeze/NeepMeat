package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
import com.neep.neepmeat.recipe.TrommelRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TrommelDisplay extends BasicDisplay
{
    private TrommelRecipe recipe;

    public TrommelDisplay(TrommelRecipe recipe)
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
        if (recipe.getAuxOutput() != null)
        {
            this.outputs.add(EntryIngredients.ofItems(List.of(recipe.getAuxOutput().resource()), (int) recipe.getAuxOutput().minAmount()));
            this.outputs.get(1).forEach(stack -> stack.tooltip(List.of(
                    Text.of("Chance: " + recipe.getAuxOutput().chance())
            )));
        }
    }

    public TrommelDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
    }

    public TrommelRecipe getRecipe()
    {
        return recipe;
    }

    public static Serializer<TrommelDisplay> serializer()
    {
        return Serializer.ofSimple(TrommelDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.TROMMEL;
    }
}
