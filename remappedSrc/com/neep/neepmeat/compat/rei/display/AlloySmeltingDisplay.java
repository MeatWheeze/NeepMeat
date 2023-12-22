package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.recipe.AlloyKilnRecipe;
import com.neep.neepmeat.recipe.GrindingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AlloySmeltingDisplay extends BasicDisplay
{
    private AlloyKilnRecipe recipe;

    public AlloySmeltingDisplay(AlloyKilnRecipe recipe)
    {
        this(
                List.of(
                        EntryIngredients.ofItems((Collection<ItemConvertible>) (Object) recipe.getItemInput1().getAll(), (int) recipe.getItemInput1().amount()),
                        EntryIngredients.ofItems((Collection<ItemConvertible>) (Object) recipe.getItemInput2().getAll(), (int) recipe.getItemInput2().amount())
                ),
                new ArrayList<>(List.of(
                        EntryIngredients.ofItems(List.of(recipe.getItemOutput().resource()), (int) recipe.getItemOutput().minAmount())
                )),
                Optional.empty()
        );
        this.recipe = recipe;
        this.outputs.get(0).forEach(stack -> stack.tooltip(List.of(
                Text.of("Min: " + recipe.getItemOutput().minAmount() + ", Max: " + recipe.getItemOutput().maxAmount())
        )));
    }

    public AlloySmeltingDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
    }

    public static Serializer<AlloySmeltingDisplay> serializer()
    {
        return Serializer.ofSimple(AlloySmeltingDisplay::new);
    }

    public int getProcessTime()
    {
        return recipe.getProcessTime();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.ALLOY_SMELTING;
    }

}
