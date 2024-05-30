package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.EntityToItemDisplay;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;

import java.util.List;

public class EntityToItemManufactureCategory extends ManufactureCategory<EntityToItemDisplay>
{
    @Override
    public CategoryIdentifier<? extends EntityToItemDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.ENTITY_TO_ITEM;
    }

    @Override
    public List<Widget> setupDisplay(EntityToItemDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.x + 5, bounds.y + 5);
        List<Widget> widgets = Lists.newArrayList();

        widgets.add(new OutlineWidget(bounds));

        var base = new LabelledEntitySlot(startPoint, Text.of("Base: "), display.getBase());
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
    public Text getTitle()
    {
        return Text.translatable("category.neepmeat.entity_to_item_manufacture");
    }

    public static int borderCol()
    {
        return PLCCols.BORDER.col;
    }

    static class LabelledEntitySlot extends Widget
    {
        private final Point origin;
        private final EntityType<?> entityType;
        private final Text name;
        private final Point slotOrigin;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        LabelledEntitySlot(Point origin, Text name, EntityType<?> entityType)
        {
            this.name = name;
            this.origin = origin;
            this.entityType = entityType;
            this.slotOrigin = new Point(origin.x + textRenderer.getWidth(name) + 2, origin.y);
        }

        public int width()
        {
            return textRenderer.getWidth(name) + 2 + 20;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            GUIUtil.drawText(context, textRenderer, name, origin.x, origin.y, borderCol(), true);

            Text entityName = entityType.getName();
            GUIUtil.drawText(context, textRenderer, entityName, slotOrigin.x, slotOrigin.y, borderCol(), true);

//            GUIUtil.renderBorder(context, slotOrigin.x - 1, slotOrigin.y - 1, 17, 17, borderCol(), 0);
//            GUIUtil.renderBorder(context, slotOrigin.x, slotOrigin.y, 15, 15, PLCCols.TRANSPARENT.col, 0);
        }

        @Override
        public List<? extends Element> children()
        {
            return List.of();
        }
    }
}
