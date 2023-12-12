package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.recipe.AlloyKilnRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class AlloySmeltingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final int processTime;

    public AlloySmeltingEmiRecipe(AlloyKilnRecipe recipe) {
        this.processTime = recipe.getProcessTime();

        this.id = recipe.getId();
        this.input = List.of(
                EmiIngredient.of(recipe.getItemInput1().getAll().stream().map(Ingredient::ofItems).map(EmiIngredient::of).toList(), recipe.getItemInput1().amount()),
                EmiIngredient.of(recipe.getItemInput2().getAll().stream().map(Ingredient::ofItems).map(EmiIngredient::of).toList(), recipe.getItemInput2().amount())
        );
        this.output = List.of(EmiStack.of(recipe.getItemOutput().resource(), recipe.getItemOutput().minAmount()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.ALLOY_SMELTING;
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

        widgets.addText(Text.translatable("category." + NeepMeat.NAMESPACE + ".alloy_smelting.time", df.format(processTime / 20d)), getDisplayWidth() - 5, 5, 0xFF404040, false).horizontalAlign(TextWidget.Alignment.END);
        widgets.addFillingArrow(startX + 24, startY + 8, processTime * 20);

        // Input slots
        widgets.addSlot(input.get(0), startX + 1, startY - 1);
        widgets.addSlot(input.get(1), startX + 1, startY + 19);

        // Output slots
        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);
    }
}
