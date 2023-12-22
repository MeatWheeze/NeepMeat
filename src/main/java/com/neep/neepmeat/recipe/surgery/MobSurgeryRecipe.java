package com.neep.neepmeat.recipe.surgery;

import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class MobSurgeryRecipe extends SurgeryRecipe
{
    public MobSurgeryRecipe(Identifier id, int w, int h, DefaultedList<RecipeInput<?>> inputs, RecipeOutputImpl<Item> output)
    {
        super(id, w, h, inputs, output);
    }
}
