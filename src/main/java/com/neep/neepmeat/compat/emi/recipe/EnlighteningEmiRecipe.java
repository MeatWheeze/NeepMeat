package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.data.DataUtil;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.recipe.EnlighteningRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnlighteningEmiRecipe implements EmiRecipe {
    private final Identifier texture = new Identifier(NeepMeat.NAMESPACE, "textures/gui/enlightenment.png");

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final long data;

    public EnlighteningEmiRecipe(EnlighteningRecipe recipe) {
        this.data = recipe.getData();

        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getItemInput().getAll().stream().map(Ingredient::ofItems).map(EmiIngredient::of).toList(), recipe.getItemInput().amount()));
        this.output = List.of(EmiStack.of(recipe.getItemOutput().resource(), recipe.getItemOutput().minAmount()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.ENLIGHTENING;
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

        widgets.addText(Text.translatable("category." + NeepMeat.NAMESPACE + ".enlightening.data", DataUtil.formatData(data)), getDisplayWidth() - 5, 5, 0xFF404040, false).horizontalAlign(TextWidget.Alignment.END);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 25, startY + 9);

        widgets.addSlot(input.get(0), startX + 1, startY + 3);

        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);

        int thingWidth = 44;
        widgets.addTexture(new EmiTexture(texture, 0, 0, thingWidth, thingWidth, thingWidth, thingWidth, thingWidth, thingWidth), startX - 24, startY - 4);

        if (output.size() > 1) {
            widgets.addSlot(output.get(1), startX + 81, startY + 9).recipeContext(this);
        }
    }
}
