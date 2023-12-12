package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class HeartExtractionEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final Text entity;

    public HeartExtractionEmiRecipe(EntityType<?> input, Item output) {
        this.entity = Text.translatable(input.getTranslationKey());

        this.id = new Identifier(NeepMeat.NAMESPACE, "heart_extraction_"+input.getUntranslatedName().toLowerCase(Locale.ROOT)); // TODO: ???
        this.input = List.of();
        this.output = List.of(EmiStack.of(output));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.HEART_EXTRACTION;
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

        widgets.addText(entity, 20, 25, 0xFFFFFF, true);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 25, startY + 9);

        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);
    }
}
