package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.ItemManufactureDisplay;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;

import java.util.List;

public class ItemManufactureCategory extends ManufactureCategory<ItemManufactureDisplay>
{
    @Override
    public CategoryIdentifier<? extends ItemManufactureDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.ITEM_MANUFACTURE;
    }

    @Override
    public List<Widget> setupDisplay(ItemManufactureDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.x + 5, bounds.y + 5);
        List<Widget> widgets = Lists.newArrayList();
//        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(new ManufactureCategory.OutlineWidget(bounds));

        var base = new LabelledSlot(startPoint, Text.of("Base: "), EntryStacks.of(display.getBase()));
        widgets.add(base);

        var output = new LabelledSlot(new Point(startPoint.x + 20 + base.width(), startPoint.y), Text.of("Output: "), display.getOutputEntries().get(0).get(0));
        widgets.add(output);

        int entryY = startPoint.y + 22;
        int entryX = startPoint.x + 1;
        for (var step : display.getSteps())
        {
            ManufactureCategory.EntryWidget widget = new ManufactureCategory.EntryWidget(new Point(entryX, entryY), step, 160 - 20);
            widgets.add(widget);
            entryY += widget.height() + 2;
        }

        return widgets;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("category.neepmeat.manufacture");
    }

    public static int borderCol()
    {
        return PLCCols.BORDER.col;
    }
}
