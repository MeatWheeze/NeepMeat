package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.compat.emi.helper.EmiIngredientHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompactingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final int page;

    public CompactingEmiRecipe(List<Item> input, Item output, int page) {
        this.page = page;

        this.id = new Identifier(NeepMeat.NAMESPACE, "compacting_"+page); // TODO: ???
        this.input = input.stream().map(Ingredient::ofItems).map(EmiIngredient::of).toList();
        this.output = List.of(EmiStack.of(output));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.COMPACTING;
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
        return 140;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int startX = getDisplayWidth() - 55;
        int startY = 110;

        int i = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 7; x++) {
                EmiIngredient ingredient = input.size() > i ? input.get(i) : EmiIngredientHelper.EMPTY;
                widgets.addSlot(ingredient, getDisplayWidth() / 2 - 72 + 9 + x * 18, 12 + y * 18);
                i++;
            }
        }

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX - 1 - 5, startY + 7 - 5);

        widgets.addSlot(output.get(0), startX + 33 - 5, startY + 8 - 5).recipeContext(this);
    }
}
