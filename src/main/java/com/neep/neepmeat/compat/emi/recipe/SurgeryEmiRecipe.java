package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.compat.emi.helper.EmiIngredientHelper;
import com.neep.neepmeat.recipe.surgery.GeneralSurgeryRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Deprecated
public class SurgeryEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public SurgeryEmiRecipe(GeneralSurgeryRecipe recipe) {
        this.id = recipe.getId();
        this.input = EmiIngredientHelper.inputsToIngredients(recipe.getInputs());
        this.output = List.of(EmiStack.of(recipe.getOutput().resource(), recipe.getOutput().minAmount()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.SURGERY;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 55;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int startX = getDisplayWidth() / 2 - 58;
        int startY = getDisplayHeight() / 2 - 27;

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 60, startY + 18);

        // TODO: ???
    }
}
