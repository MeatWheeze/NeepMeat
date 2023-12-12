package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.compat.emi.helper.EmiIngredientHelper;
import com.neep.neepmeat.recipe.TrommelRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class TrommelEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private List<EmiStack> output;

    public TrommelEmiRecipe(TrommelRecipe recipe) {
        this.id = recipe.getId();
        this.input = EmiIngredientHelper.inputToIngredient(recipe.getFluidInput());
        this.output = List.of(EmiStack.of(recipe.getFluidOutput().resource(), recipe.getFluidOutput().minAmount()));

        if (recipe.getAuxOutput() != null) {
            this.output = Stream.concat(this.output.stream(), Stream.of(EmiStack.of(recipe.getAuxOutput().resource(), recipe.getAuxOutput().amount()))).toList();
            // TODO: tooltip:
            // Text.of("Chance: " + recipe.getAuxOutput().chance())
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.TROMMEL;
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

        // Auxiliary output slot
        if (output.size() > 1) {
            widgets.addSlot(output.get(1), startX + 87, startY + 9).recipeContext(this);
        }
    }
}
