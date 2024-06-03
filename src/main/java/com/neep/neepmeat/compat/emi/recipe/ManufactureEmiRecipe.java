package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.recipe.ImplantStep;
import com.neep.neepmeat.plc.recipe.InjectStep;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public abstract class ManufactureEmiRecipe<T> implements EmiRecipe
{
    protected final T base;
    protected final List<ManufactureStep<?>> steps;

    protected ManufactureEmiRecipe(T base, List<ManufactureStep<?>> steps)
    {
        this.base = base;
        this.steps = steps;
    }

    static void drawThing(int x, int y, ManufactureStep<?> step, WidgetHolder widgets)
    {
        if (step instanceof CombineStep combineStep)
        {
            widgets.addSlot(EmiStack.of(combineStep.getItem()), x - 1, y + 1).drawBack(false);
        }
        else if (step instanceof InjectStep injectStep)
        {
            widgets.addSlot(EmiStack.of(injectStep.getFluid()), x - 1, y + 1).drawBack(false);
        }
        else if (step instanceof ImplantStep implantStep)
        {
            widgets.addSlot(EmiStack.of(implantStep.getItem()), x - 1, y + 1).drawBack(false);
        }
        else
        {
            widgets.addSlot(EmiStack.EMPTY, x, y + 2).drawBack(false);
        }
    }

    static void appendStepIngredients(List<ManufactureStep<?>> steps, List<EmiIngredient> ingredients)
    {
        for (var step : steps)
        {
            if (step instanceof CombineStep combineStep)
            {
                ingredients.add(EmiStack.of(combineStep.getItem()));
            }
            else if (step instanceof InjectStep injectStep)
            {
                ingredients.add(EmiStack.of(injectStep.getFluid()));
            }
            else if (step instanceof ImplantStep implantStep)
            {
                ingredients.add(EmiStack.of(implantStep.getItem()));
            }
        }
    }

    public static int borderCol()
    {
        return PLCCols.BORDER.col;
    }

    public T getBase()
    {
        return base;
    }

    @Override
    public int getDisplayWidth()
    {
        return 160;
    }

    @Override
    public int getDisplayHeight()
    {
        return 150;
    }

    static class LabelledSlot extends Widget
    {
        private final int originX;
        private final int originY;
        private final Text name;
        private final int slotOriginX;
        private final int slotOriginY;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public LabelledSlot(int originX, int originY, Text name, EmiStack stack, WidgetHolder widgets)
        {
            this(originX, originY, name, stack, widgets, null);
        }

        public LabelledSlot(int originX, int originY, Text name, EmiStack stack, WidgetHolder widgets, EmiRecipe recipe)
        {
            this.name = name;
            this.originX = originX;
            this.originY = originY;
            this.slotOriginX = originX + textRenderer.getWidth(name) + 2;
            this.slotOriginY = originY;

            widgets.addSlot(stack, slotOriginX, slotOriginY).drawBack(false).recipeContext(recipe);
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
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            textRenderer.drawWithShadow(matrices, name, originX, originY, borderCol());
            GUIUtil.renderBorder(matrices, slotOriginX, slotOriginY, 17, 17, borderCol(), 0);
            GUIUtil.renderBorder(matrices, slotOriginX + 1, slotOriginY + 1, 15, 15, PLCCols.TRANSPARENT.col, 0);
        }
    }

    public static class EntryWidget extends Widget
    {
        private final int originX;
        private final int originY;
        private final ManufactureStep<?> step;
        private final Text name;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        private final int width;

        public EntryWidget(int originX, int originY, ManufactureStep<?> step, int width, WidgetHolder widgets)
        {
            this.originX = originX;
            this.originY = originY;
            this.step = step;
            this.name = step.getName();
            this.width = width;

            drawThing(originX + width() - 14, originY, step, widgets);
        }

        public int height()
        {
            return Math.max(textRenderer.fontHeight + 3, 19);
        }

        public int width()
        {
            return width;
        }

        @Override
        public Bounds getBounds()
        {
            return new Bounds(originX, originY, width(), height());
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            int x = originX + 2;
            int y = originY + 2;

            textRenderer.drawWithShadow(matrices, name, x, y, borderCol());
            GUIUtil.renderBorder(matrices, originX, originY, width() + 3, height(), borderCol(), 0);
        }
    }

    static class OutlineWidget extends Widget
    {
        private final Bounds bounds;

        public OutlineWidget(Bounds bounds)
        {
            this.bounds = bounds;
        }

        @Override
        public Bounds getBounds()
        {
            return this.bounds;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            DrawableHelper.fill(matrices, bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height(), 0xFF000000);
            GUIUtil.renderBorder(matrices, bounds.x(), bounds.y(), bounds.width(), bounds.height(), borderCol(), 0);
            GUIUtil.renderBorder(matrices, bounds.x() + 1, bounds.y() + 1, bounds.width() - 2, bounds.height() - 2, PLCCols.TRANSPARENT.col, 0);
        }
    }
}
