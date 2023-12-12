package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.recipe.GrindingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Stream;

public class GrindingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private List<EmiStack> output;

    public GrindingEmiRecipe(GrindingRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getItemInput().getAll().stream().map(Ingredient::ofItems).map(EmiIngredient::of).toList(), recipe.getItemInput().amount()));
        this.output = List.of(EmiStack.of(recipe.getItemOutput().resource(), recipe.getItemOutput().minAmount()));

        // TODO: append tooltip to output:
        // Text.of("Min: " + recipe.getItemOutput().minAmount() + ", Max: " + recipe.getItemOutput().maxAmount())

        if (recipe.getAuxOutput() != null) {
            this.output = Stream.concat(this.output.stream(), Stream.of(EmiStack.of(recipe.getAuxOutput().resource(), recipe.getAuxOutput().amount()))).toList();
            // TODO: tooltip
            // Text.of("Min: " + recipe.getItemOutput().minAmount() + ", Max: " + recipe.getItemOutput().maxAmount())
            // Text.of("Chance: " + recipe.getItemOutput().chance())
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.GRINDING;
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
        DecimalFormat df = new DecimalFormat("###.##");

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 25, startY + 9);

        widgets.addSlot(input.get(0), startX + 1, startY + 9);

        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);

        if (output.size() > 1) {
            widgets.addSlot(output.get(1), startX + 81, startY + 9).recipeContext(this);
        }
    }
}
