package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.recipe.EnlighteningRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EnlighteningDisplay extends BasicDisplay
{
    private EnlighteningRecipe recipe;

    public EnlighteningDisplay(EnlighteningRecipe recipe)
    {
        this(
                List.of(EntryIngredients.ofItems((Collection<ItemConvertible>) (Object) recipe.getItemInput().getAll(), (int) recipe.getItemInput().amount())),
                new ArrayList<>(List.of(
                        EntryIngredients.ofItems(List.of(recipe.getItemOutput().resource()), (int) recipe.getItemOutput().minAmount())
                )),
                Optional.empty()
        );
        this.recipe = recipe;
    }

    public EnlighteningDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
    }

    public static Serializer<EnlighteningDisplay> serializer()
    {
        return Serializer.ofSimple(EnlighteningDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.ENLIGHTENING;
    }

    public long getData()
    {
        return recipe.getData();
    }
}
