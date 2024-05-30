package com.neep.neepmeat.compat.rei.category;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.compat.rei.display.ManufactureDisplay;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.recipe.ImplantStep;
import com.neep.neepmeat.plc.recipe.InjectStep;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public abstract class ManufactureCategory<T extends ManufactureDisplay<?>> implements DisplayCategory<T>
{
    static Widget jankThing(int x, int y, ManufactureStep<?> step)
    {
        if (step instanceof CombineStep combineStep)
        {
            return Widgets.createSlot(new Point(x, y + 2))
                    .entries( Collections.singleton(EntryStacks.of(combineStep.getItem())))
                    .disableBackground();
        }
        else if (step instanceof InjectStep injectStep)
        {
            return Widgets.createSlot(new Point(x, y + 2))
                    .entries( Collections.singleton(EntryStacks.of(injectStep.getFluid())))
                    .disableBackground();
        }
        else if (step instanceof ImplantStep implantStep)
        {
            return Widgets.createSlot(new Point(x, y + 2))
                    .entries( Collections.singleton(EntryStacks.of(implantStep.getItem())))
                    .disableBackground();
        }
        return Widgets.createSlot(new Point(x, y + 2)).disableBackground();
    }

    @Override
    public int getDisplayHeight()
    {
        return 150;
    }

    @Override
    public int getDisplayWidth(T display)
    {
        return 160;
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(PLCBlocks.PLC);
    }

    public static class EntryWidget extends Widget
    {
        private final Point origin;
        private final ManufactureStep<?> step;
        private final Widget widget;
        private final Text name;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        private final int width;

        public EntryWidget(Point origin, ManufactureStep<?> step, int width)
        {
            this.origin = origin;
            this.step = step;
            this.name = step.getName();
            this.width = width;

            this.widget = jankThing(origin.x + width() - 14, origin.y, step);
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
        public List<? extends Element> children()
        {
            return List.of(widget);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            int x = origin.x + 2;
            int y = origin.y + 2;
            int maxWidth = width();

            GUIUtil.drawText(context, textRenderer, name, x, y, ItemManufactureCategory.borderCol(), true);

            GUIUtil.renderBorder(context, origin.x, origin.y, width() + 3, height(), ItemManufactureCategory.borderCol(), 0);
            widget.render(context, mouseX, mouseY, delta);
        }
    }

    static class OutlineWidget extends Widget
    {
        private final Rectangle bounds;

        public OutlineWidget(Rectangle bounds)
        {
            this.bounds = bounds;
        }

        @Override
        public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
        {
            matrices.fill(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0xFF000000);
            GUIUtil.renderBorder(matrices, bounds.x, bounds.y, bounds.width, bounds.height, ItemManufactureCategory.borderCol(), 0);
            GUIUtil.renderBorder(matrices, bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2, PLCCols.TRANSPARENT.col, 0);
        }

        @Override
        public List<? extends Element> children()
        {
            return List.of();
        }
    }

    static class LabelledSlot extends Widget
    {
        private final Slot slot;
        private final Point origin;
        private final Text name;
        private final Point slotOrigin;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        LabelledSlot(Point origin, Text name, EntryStack<?> stack)
        {
            this.name = name;
            this.origin = origin;
            this.slotOrigin = new Point(origin.x + textRenderer.getWidth(name) + 2, origin.y);
            this.slot = Widgets.createSlot(slotOrigin)
                    .entries(Collections.singleton(stack))
                    .disableBackground();
        }

        public int width()
        {
            return textRenderer.getWidth(name) + 2 + 20;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            GUIUtil.drawText(context, textRenderer, name, origin.x, origin.y, EntityToItemManufactureCategory.borderCol(), true);
            slot.render(context, mouseX, mouseY, delta);
            GUIUtil.renderBorder(context, slotOrigin.x - 1, slotOrigin.y - 1, 17, 17, EntityToItemManufactureCategory.borderCol(), 0);
            GUIUtil.renderBorder(context, slotOrigin.x, slotOrigin.y, 15, 15, PLCCols.TRANSPARENT.col, 0);
        }

        @Override
        public List<? extends Element> children()
        {
            return List.of(slot);
        }
    }
}
