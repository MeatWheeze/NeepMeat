package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.recipe.GrindingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class GrindingDisplay extends BasicDisplay
{
    private final CategoryIdentifier<?> categoryIdentifier;
    private GrindingRecipe recipe;

    public GrindingDisplay(CategoryIdentifier<?> categoryIdentifier, GrindingRecipe recipe)
    {
        this(categoryIdentifier,
                List.of(EntryIngredients.ofItems((Collection<ItemConvertible>) (Object) recipe.getItemInput().getAll(), (int) recipe.getItemInput().amount())),
                new ArrayList<>(List.of(
                        EntryIngredients.ofItems(List.of(recipe.getItemOutput().resource()), (int) recipe.getItemOutput().minAmount())
                )),
                Optional.empty()
        );
        this.recipe = recipe;
        this.outputs.get(0).forEach(stack -> stack.tooltip(List.of(
                Text.of("Min: " + recipe.getItemOutput().minAmount() + ", Max: " + recipe.getItemOutput().maxAmount())
//                Text.of("Chance: " + recipe.getItemOutput().chance())
        )));
        if (recipe.getAuxOutput() != null)
        {
            this.outputs.add(EntryIngredients.ofItems(List.of(recipe.getAuxOutput().resource()), (int) recipe.getAuxOutput().minAmount()));
            this.outputs.get(1).forEach(stack -> stack.tooltip(List.of(
//                Text.of("Min: " + recipe.getItemOutput().minAmount() + ", Max: " + recipe.getItemOutput().maxAmount()),
                    Text.of("Chance: " + recipe.getItemOutput().chance())
            )));
        }
    }

    public GrindingDisplay(CategoryIdentifier<?> categoryIdentifier, List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
        this.categoryIdentifier = categoryIdentifier;
    }

    public static BasicDisplay.Serializer<GrindingDisplay> serializer(CategoryIdentifier<?> categoryIdentifier)
    {
        return BasicDisplay.Serializer.ofSimple((input, output, location1) -> new GrindingDisplay(categoryIdentifier, input, output, location1));
    }

    public static <T extends GrindingRecipe> Function<T, GrindingDisplay> filler(CategoryIdentifier<? extends GrindingDisplay> categoryIdentifier)
    {
        return r -> new GrindingDisplay(categoryIdentifier, r);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.GRINDING;
    }

}
