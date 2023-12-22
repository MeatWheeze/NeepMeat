package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.compat.emi.helper.EmiIngredientHelper;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
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
import java.util.stream.Stream;

public class MixingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final int processTime;

    public MixingEmiRecipe(MixingRecipe recipe) {
        this.processTime = recipe.getProcessTime();

        this.id = recipe.getId();
        this.input = Stream.of(
                EmiIngredientHelper.inputToIngredient(recipe.getFluidInputs().get(0)),
                EmiIngredientHelper.inputToIngredient(recipe.getFluidInputs().get(1)),
                List.of(EmiIngredient.of(recipe.getItemIngredient().getAll().stream().map(Ingredient::ofItems).map(EmiIngredient::of).toList(), recipe.getItemIngredient().amount()))
        ).flatMap(List::stream).toList(); // TODO: something does not seem right here
        this.output = List.of(EmiStack.of(recipe.getFluidOutput().resource(), recipe.getFluidOutput().minAmount()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.MIXING;
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

        widgets.addText(Text.translatable("category." + NeepMeat.NAMESPACE + ".mixing.time", df.format(processTime / 20d)), getDisplayWidth() - 5, 5, 0xFF404040, false).horizontalAlign(TextWidget.Alignment.END);
        widgets.addFillingArrow(startX + 24, startY + 8, processTime * 1000 / 20);

        // Input slots
        if (!input.get(1).isEmpty()) {
            widgets.addSlot(input.get(0), startX + 1, startY - 1);
            widgets.addSlot(input.get(1), startX + 1, startY + 19);
        } else {
            widgets.addSlot(input.get(0), startX + 1, startY + 9);
        }

        if (input.get(2).getAmount() != 0) {
            widgets.addSlot(input.get(2), startX - 20, startY + 9);
        }

        // Output slots
        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);
    }
}
