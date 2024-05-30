package com.neep.neepmeat.compat.emi.recipe;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.plc.recipe.ItemManufactureRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemManufactureEmiRecipe extends ManufactureEmiRecipe<Item>
{
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public ItemManufactureEmiRecipe(ItemManufactureRecipe recipe)
    {
        super(recipe.getBase(), recipe.getSteps());

        List<EmiIngredient> inputs = Lists.newArrayList();
        appendStepIngredients(steps, inputs);

        this.id = recipe.getId();
        this.input = inputs;
        this.output = List.of(EmiStack.of(recipe.getRecipeOutput().resource(), recipe.getRecipeOutput().minAmount()));
    }

    public static int borderCol()
    {
        return PLCCols.BORDER.col;
    }

    @Override
    public EmiRecipeCategory getCategory()
    {
        return NMEmiPlugin.ITEM_MANUFACTURE;
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
    public void addWidgets(WidgetHolder widgets)
    {
        int startX = 5;
        int startY = 5;

        widgets.add(new OutlineWidget(new Bounds(0, 0, getDisplayWidth(), getDisplayHeight())));

        var widgetBase = new LabelledSlot(startX, startY, Text.of("Base: "), EmiStack.of(getBase()), widgets);
        widgets.add(widgetBase);

        var widgetOutput = new LabelledSlot(startX + 20 + widgetBase.width(), startY, Text.of("Output: "), output.get(0), widgets, this);
        widgets.add(widgetOutput);

        int entryX = startX + 1;
        int entryY = startY + 22;
        for (var step : steps)
        {
            var widget = new EntryWidget(entryX, entryY, step, getDisplayWidth() - 20, widgets);
            widgets.add(widget);
            entryY += widget.height() + 2;
        }
    }
}
