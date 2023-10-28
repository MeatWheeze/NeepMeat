package com.neep.neepmeat.compat.rei.display;

import com.neep.meatlib.recipe.ingredient.FluidIngredient;
import com.neep.meatlib.recipe.ingredient.ItemIngredient;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MixingDisplay extends BasicDisplay
{
    private MixingRecipe recipe;
    public int processTime;

    public MixingDisplay(MixingRecipe recipe)
    {
        this(
                List.of(
                        SurgeryDisplay.entryFromInput(recipe.getFluidInputs().get(0)),
                        SurgeryDisplay.entryFromInput(recipe.getFluidInputs().get(1)),
                        SurgeryDisplay.entryFromInput(recipe.getItemIngredient())
                        ),
                Collections.singletonList(EntryIngredients.of(recipe.getFluidOutput().resource(), recipe.getFluidOutput().minAmount())),
                Optional.empty()
        );
        this.recipe = recipe;
        this.processTime = recipe.getProcessTime();
    }

    public MixingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
    }

    public MixingRecipe getRecipe()
    {
        return recipe;
    }

    public static Serializer<MixingDisplay> serializer()
    {
        return Serializer.ofSimple(MixingDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.MIXING;
    }

    public int getProcessTime()
    {
        return processTime;
    }
}
