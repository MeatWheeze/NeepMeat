package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.recipe.GrindingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrindingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final EmiRecipeCategory category;
    private final GrindingRecipe recipe;

    public GrindingEmiRecipe(EmiRecipeCategory category, GrindingRecipe recipe) {
        this.category = category;
        this.recipe = recipe;

        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getItemInput().getAll().stream().map(Ingredient::ofItems).map(EmiIngredient::of).toList(), recipe.getItemInput().amount()));

        List<EmiStack> output = new ArrayList<>();

        if (!recipe.destroy())
        {
            output.add(EmiStack.of(recipe.getItemOutput().resource(), recipe.getItemOutput().minAmount()));
            if (recipe.getAuxOutput() != null) {
                output.add(EmiStack.of(recipe.getAuxOutput().resource(), recipe.getAuxOutput().amount()));
            }
        }

        this.output = output;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
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
        if (recipe.destroy())
        {
            return;
        }

        int startX = getDisplayWidth() / 2 - 41;
        int startY = 10;

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 25, startY + 9);

        widgets.addSlot(input.get(0), startX + 1, startY + 9);

        widgets.addSlot(output.get(0), startX + 61, startY + 9).appendTooltip(Text.of("Min: " + recipe.getItemOutput().minAmount() + ", Max: " + recipe.getItemOutput().maxAmount())).recipeContext(this);

        if (output.size() > 1) {
            float chance = recipe.getItemOutput().chance();
            widgets.addSlot(output.get(1), startX + 81, startY + 9).appendTooltip(Text.of("Chance: " + recipe.getAuxOutput().chance())).recipeContext(this);
        }
    }
}
