package com.neep.neepmeat.compat.emi.recipe;

import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.api.processing.BlockCrushingRegistry;
import com.neep.neepmeat.compat.emi.helper.EmiIngredientHelper;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.BlockCrushingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockCrushingEmiRecipe implements EmiRecipe
{
    private final EmiRecipeCategory category;
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final RecipeOutput<?> recipeOutput;
    private final EmiStack output;
    private final EmiStack extraOutput;

    public BlockCrushingEmiRecipe(int index, EmiRecipeCategory category, BlockCrushingRecipe recipe, BlockCrushingRegistry.Entry entry)
    {
        this.category = category;
        this.id = new Identifier(recipe.getId().getNamespace(), recipe.getId().getPath() + "_" + index);
        this.input = EmiIngredientHelper.inputToIngredient(entry.input());
        this.recipeOutput = entry.output();
        this.output = EmiStack.of(entry.output().resource(), entry.output().minAmount());
        this.extraOutput = EmiStack.of(entry.extra().resource(), entry.extra().minAmount()).setChance(entry.extra().chance());

    }

    @Override
    public EmiRecipeCategory getCategory()
    {
        return category;
    }

    @Override
    public @Nullable Identifier getId()
    {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs()
    {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs()
    {
        return List.of(output, extraOutput);
    }

    @Override
    public int getDisplayWidth()
    {
        return 150;
    }

    @Override
    public int getDisplayHeight()
    {
        return 55;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        int startX = getDisplayWidth() / 2 - 41;
        int startY = 10;

        widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 25, startY + 9);

        widgets.addSlot(getInputs().get(0), startX + 1, startY + 9);

        if (!output.isEmpty())
        {
            long amount = output.getAmount();

            widgets.addSlot(EmiIngredient.of(List.of(output), amount), startX + 61, startY + 9)
                    .recipeContext(this)
                    .appendTooltip(NMrecipeTypes.ofMinMax(recipeOutput.minAmount(), recipeOutput.maxAmount()));

            widgets.addSlot(EmiIngredient.of(List.of(extraOutput)), startX + 81, startY + 9).recipeContext(this);
        }
    }
}
