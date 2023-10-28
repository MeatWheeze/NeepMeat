package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.recipe.surgery.TransformingToolRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class TransformingToolDisplay extends SurgeryDisplay
{
    public TransformingToolDisplay(TransformingToolRecipe recipe)
    {
        super(recipe);
    }

    public TransformingToolDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output, location);
    }

    public static Serializer<TransformingToolDisplay> serializer()
    {
        return Serializer.ofSimple(TransformingToolDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.TRANSFORMING_TOOL;
    }

}
