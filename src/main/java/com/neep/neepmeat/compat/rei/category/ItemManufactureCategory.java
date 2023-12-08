package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.client.screen.plc.PLCProgramScreen;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
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
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import software.bernie.geckolib3.core.util.Color;

import java.util.Collections;
import java.util.List;

public class ItemManufactureCategory implements DisplayCategory<ManufactureDisplay>
{
    @Override
    public CategoryIdentifier<? extends ManufactureDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.MANUFACTURE;
    }

    @Override
    public List<Widget> setupDisplay(ManufactureDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.x + 5, bounds.y + 5);
        List<Widget> widgets = Lists.newArrayList();
//        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(new OutlineWidget(bounds));

        var base = new LabelledSlot(startPoint, Text.of("Base: "), EntryStacks.of(display.getBase()));
        widgets.add(base);

        var output = new LabelledSlot(new Point(startPoint.x + 20 + base.width(), startPoint.y), Text.of("Output: "), display.getOutputEntries().get(0).get(0));
        widgets.add(output);

        int entryY = startPoint.y + 22;
        int entryX = startPoint.x + 1;
        for (var step : display.getSteps())
        {
            EntryWidget widget = new EntryWidget(new Point(entryX, entryY), step, 160 - 20);
            widgets.add(widget);
            entryY += widget.height() + 2;
        }

        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 150;
    }

    @Override
    public int getDisplayWidth(ManufactureDisplay display)
    {
        return 160;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("category.neepmeat.manufacture");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(PLCBlocks.SURGERY_CONTROLLER);
    }

    public static int borderCol()
    {
        return PLCProgramScreen.borderCol();
    }

    public static int transparentCol()
    {
        return Color.ofRGBA(255, 94, 33, 100).getColor();
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

//        public int height()
//        {
//            return Math.max(textRenderer.fontHeight, )
//        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            textRenderer.drawWithShadow(matrices, name, origin.x, origin.y, borderCol());
            slot.render(matrices, mouseX, mouseY, delta);
            GUIUtil.renderBorder(matrices, slotOrigin.x - 1, slotOrigin.y - 1, 17, 17, borderCol(), 0);
            GUIUtil.renderBorder(matrices, slotOrigin.x, slotOrigin.y, 15, 15, transparentCol(), 0);
        }

        @Override
        public List<? extends Element> children()
        {
            return List.of(slot);
        }
    }

    static class EntryWidget extends Widget
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

            this.widget = getThing(origin.x + width() - 14, origin.y, step);
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
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            int x = origin.x + 2;
            int y = origin.y + 2;
            int maxWidth = width();

            textRenderer.drawWithShadow(matrices, name, x, y, borderCol());

            GUIUtil.renderBorder(matrices, origin.x, origin.y, width() + 3, height(), borderCol(), 0);
            widget.render(matrices, mouseX, mouseY, delta);
        }
    }

    static Widget getThing(int x, int y, ManufactureStep<?> step)
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

    static class OutlineWidget extends Widget
    {
        private final Rectangle bounds;

        public OutlineWidget(Rectangle bounds)
        {
            this.bounds = bounds;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            DrawableHelper.fill(matrices, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0xFF000000);
            GUIUtil.renderBorder(matrices, bounds.x, bounds.y, bounds.width, bounds.height, borderCol(), 0);
            GUIUtil.renderBorder(matrices, bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2, transparentCol(), 0);
        }

        @Override
        public List<? extends Element> children()
        {
            return List.of();
        }
    }

//    static StepIcon getIcon(ManufactureStep<?> step)
//    {
//        if (step instanceof CombineStep combineStep)
//        {
//            return new ItemIcon(combineStep.getItem());
//        }
//        return StepIcon.empty();
//    }
//
//    interface StepIcon
//    {
//        StepIcon EMPTY = (m, x, y) -> {};
//
//        void render(MatrixStack matrices, float x, float y);
//
//        static StepIcon empty()
//        {
//            return EMPTY;
//        }
//
//    }
//
//    static class ItemIcon implements StepIcon
//    {
//        private final Item item;
//        private final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
//
//        ItemIcon(Item item)
//        {
//            this.item = item;
//        }
//
//        @Override
//        public void render(MatrixStack matrices, float x, float y)
//        {
//            itemRenderer.renderItem(item.getDefaultStack(), ModelTransformation.Mode.GUI, );
//        }
//    }
}
