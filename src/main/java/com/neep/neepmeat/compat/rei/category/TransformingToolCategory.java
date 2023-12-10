package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.TransformingToolDisplay;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.recipe.InjectStep;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class TransformingToolCategory implements DisplayCategory<TransformingToolDisplay>
{

    public static final Identifier GHOST_AXE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/ghost_axe.png");
    public static final Identifier GHOST_SWORD = new Identifier(NeepMeat.NAMESPACE, "textures/gui/ghost_sword.png");

    @Override
    public CategoryIdentifier<TransformingToolDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.TRANSFORMING_TOOL;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("category." + NeepMeat.NAMESPACE + ".transforming_tool");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(PLCBlocks.SURGERY_CONTROLLER);
    }

    @Override
    public List<Widget> setupDisplay(TransformingToolDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.x + 5, bounds.y + 5);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(new ItemManufactureCategory.OutlineWidget(bounds));

        var base = new ItemManufactureCategory.LabelledSlot(startPoint, Text.of("Base: "), EntryStacks.of(display.getBase()));
        widgets.add(base);

//        var output = new ItemManufactureCategory.LabelledSlot(new Point(startPoint.x + 20 + base.width(), startPoint.y), Text.of("Output: "), );
//        widgets.add(output);

        int entryY = startPoint.y + 22;
        int entryX = startPoint.x + 1;

        ToolWidget toolWidget;
        toolWidget = new ToolWidget(new Point(entryX, entryY), 160 - 20, GHOST_AXE);
        widgets.add(toolWidget);
        entryY += toolWidget.height() + 2;
        toolWidget = new ToolWidget(new Point(entryX, entryY), 160 - 20, GHOST_SWORD);
        widgets.add(toolWidget);
        entryY += toolWidget.height() + 2;
        ItemManufactureCategory.EntryWidget entryWidget = new ItemManufactureCategory.EntryWidget(new Point(entryX, entryY),
                new InjectStep(FluidVariant.of(NMFluids.STILL_WORK_FLUID)), 160 - 20);
        widgets.add(entryWidget);

        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 100;
    }

    @Override
    public int getDisplayWidth(TransformingToolDisplay display)
    {
        return 160;
    }

    public static class ToolWidget extends Widget
    {
        private final Point origin;
        private final Text name;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        private final int width;
        private final Widget widget;

        public ToolWidget(Point origin, int width, Identifier texture)
        {
            var step = new CombineStep(ItemVariant.blank());
            this.origin = origin;
            this.name = step.getName();
            this.width = width;

            this.widget = Widgets.wrapVanillaWidget(new TexturedButtonWidget(
                    origin.x + width() - 14, origin.y, 16, 16, 0, 0, 0,
                    texture, 16, 16, b -> {}));
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

            textRenderer.drawWithShadow(matrices, name, x, y, PLCCols.BORDER.col);

            GUIUtil.renderBorder(matrices, origin.x, origin.y, width() + 3, height(), PLCCols.BORDER.col, 0);
            widget.render(matrices, mouseX, mouseY, delta);
        }
    }
}
