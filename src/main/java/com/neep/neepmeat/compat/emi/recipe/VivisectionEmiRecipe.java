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
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class VivisectionEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final List<Text> entities;

    public VivisectionEmiRecipe(List<EntityType<?>> input, Item output) {
        this.entities = input.stream().map(e -> (Text) Text.translatable(e.getTranslationKey())).toList();

        this.id = new Identifier(NeepMeat.NAMESPACE, "vivisection/"+String.join("_", input.stream().map(e -> e.getUntranslatedName().toLowerCase(Locale.ROOT)).toList()));
        this.input = List.of();
        this.output = List.of(EmiStack.of(output));
    }

    public VivisectionEmiRecipe(Item input, Item output) {
        this.entities = List.of();

        this.id = new Identifier(NeepMeat.NAMESPACE, "vivisection/"+ Registries.ITEM.getId(input).getPath().toLowerCase(Locale.ROOT));
        this.input = List.of(EmiIngredient.of(Ingredient.ofItems(input)));
        this.output = List.of(EmiStack.of(output));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.VIVISECTION;
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

        if (!entities.isEmpty()) {
            widgets.addText(entities.get(0), 35, 5, 0xFFFFFF, true);
        } else if (!input.isEmpty()) {
            widgets.addSlot(input.get(0), startX, startY + 9);
        }

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 25, startY + 9);

        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);
    }
}
