package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.compat.emi.helper.EmiIngredientHelper;
import com.neep.neepmeat.recipe.FluidHeatingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeatingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public HeatingEmiRecipe(FluidHeatingRecipe recipe) {
        this.id = recipe.getId();
        this.input = EmiIngredientHelper.inputToIngredient(recipe.getFluidInput());
        this.output = List.of(EmiStack.of(recipe.getFluidOutput().resource(), recipe.getFluidOutput().minAmount()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.HEATING;
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
        int startX = getDisplayWidth() / 2 - 41;
        int startY = 10;

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 24, startY + 8);

        // Input slot
        widgets.addSlot(input.get(0), startX + 1, startY + 9);

        // Output slot
        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);
    }
}
