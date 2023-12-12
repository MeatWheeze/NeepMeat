package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.recipe.EnlighteningRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class EnlighteningEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final float data;

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
        DecimalFormat df = new DecimalFormat("###.##");

        widgets.addText(Text.translatable("category." + NeepMeat.NAMESPACE + ".enlightening.data", df.format(data)), getDisplayWidth() - 5, 5, 0xFF404040, false).horizontalAlign(TextWidget.Alignment.END);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 25, startY + 9);

        widgets.addSlot(input.get(0), startX + 1, startY + 9);

        widgets.addSlot(output.get(0), startX + 61, startY + 9).recipeContext(this);

        // TODO: unsure of x, y, width, height?
        widgets.addDrawable(0, 0, getDisplayWidth(), getDisplayHeight(), (matrices, mouseX, mouseY, delta) -> MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(NMBlocks.PEDESTAL), ModelTransformation.Mode.GUI, 255, 1, matrices, MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers(), 0));

        if (output.size() > 1) {
            widgets.addSlot(output.get(1), startX + 81, startY + 9).recipeContext(this);
        }
    }
}
