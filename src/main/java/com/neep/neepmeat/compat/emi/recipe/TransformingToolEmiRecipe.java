package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.recipe.surgery.TransformingToolRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.WidgetHolder;

@Deprecated
public class TransformingToolEmiRecipe extends SurgeryEmiRecipe {
    public TransformingToolEmiRecipe(TransformingToolRecipe recipe) {
        super(recipe);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.TRANSFORMING_TOOL;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int startX = getDisplayWidth() / 2 - 58;
        int startY = getDisplayHeight() / 2 - 27;

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 60, startY + 18);

        // TODO: ???
    }
}
