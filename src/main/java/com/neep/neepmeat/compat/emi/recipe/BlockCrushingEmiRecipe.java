package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.api.processing.BlockCrushingRegistry;
import com.neep.neepmeat.compat.emi.helper.EmiIngredientHelper;
import com.neep.neepmeat.recipe.BlockCrushingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BlockCrushingEmiRecipe implements EmiRecipe
{
    private final EmiRecipeCategory category;
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final List<EmiStack> extraOutput;

    public BlockCrushingEmiRecipe(EmiRecipeCategory category, BlockCrushingRecipe recipe, Supplier<Collection<BlockCrushingRegistry.Entry>> entrySupplier)
    {
        this.category = category;
        this.id = recipe.getId();
        this.input = List.of(
                EmiIngredient.of(
                        entrySupplier.get().stream()
                                .map(e -> EmiIngredientHelper.inputToIngredient(e.input()))
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .toList()
                )
        );
        this.output = entrySupplier.get().stream().map(e -> EmiStack.of(e.output().resource(), e.output().minAmount())).toList();
        this.extraOutput = entrySupplier.get().stream().map(e -> EmiStack.of(e.extra().resource(), e.extra().minAmount()).setChance(e.extra().chance())).toList();
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
        return output;
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
            long amount = output.get(0).getAmount();

            widgets.addSlot(EmiIngredient.of(output, amount), startX + 61, startY + 9).recipeContext(this);

            widgets.addSlot(EmiIngredient.of(extraOutput), startX + 81, startY + 9).recipeContext(this);
        }
    }
}
