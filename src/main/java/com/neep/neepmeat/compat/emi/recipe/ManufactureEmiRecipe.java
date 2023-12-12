package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.plc.recipe.ItemManufactureRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ManufactureEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final Item base;
    private final List<ManufactureStep<?>> steps;

    public ManufactureEmiRecipe(ItemManufactureRecipe recipe) {
        this.base = (Item) recipe.getBase();
        this.steps = recipe.getSteps();

        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(Ingredient.ofItems(base)));
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
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 150;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int startX = 5;
        int startY = 5;

        // TODO: ???
    }
}
