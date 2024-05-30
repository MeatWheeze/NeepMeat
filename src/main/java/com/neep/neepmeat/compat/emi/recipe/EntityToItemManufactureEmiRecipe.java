package com.neep.neepmeat.compat.emi.recipe;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.plc.recipe.EntityToItemRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityToItemManufactureEmiRecipe extends ManufactureEmiRecipe<EntityType<?>>
{
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public EntityToItemManufactureEmiRecipe(EntityToItemRecipe recipe)
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

        var widgetBase = new LabelledEntitySlot(startX, startY, Text.of("Base: "), base, widgets);
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

    static class LabelledEntitySlot extends Widget
    {
        private final int originX;
        private final int originY;
        private final EntityType<?> entityType;
        private final Text name;
        private final int slotOriginX;
        private final int slotOriginY;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public LabelledEntitySlot(int originX, int originY, Text name, EntityType<?> entityType, WidgetHolder widgets)
        {
            this.name = name;
            this.originX = originX;
            this.originY = originY;
            this.entityType = entityType;
            this.slotOriginX = originX + textRenderer.getWidth(name) + 2;
            this.slotOriginY = originY;
        }

        public int height()
        {
            return Math.max(textRenderer.fontHeight + 3, 19);
        }

        public int width()
        {
            return textRenderer.getWidth(name) + 2 + 20;
        }

        @Override
        public Bounds getBounds()
        {
            return new Bounds(originX, originY, width(), height());
        }

        @Override
        public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
        {
            matrices.drawText(textRenderer, name, originX, originY, ItemManufactureEmiRecipe.borderCol(), true);
            matrices.drawText(textRenderer, entityType.getName(), slotOriginX, slotOriginY, ItemManufactureEmiRecipe.borderCol(), true);
//            GUIUtil.renderBorder(matrices, slotOriginX, slotOriginY, 17, 17, ItemManufactureEmiRecipe.borderCol(), 0);
//            GUIUtil.renderBorder(matrices, slotOriginX + 1, slotOriginY + 1, 15, 15, PLCCols.TRANSPARENT.col, 0);
        }
    }
}
