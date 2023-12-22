package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.MixingDisplay;
import com.neep.neepmeat.compat.rei.display.TrommelDisplay;
import com.neep.neepmeat.init.NMBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.text.DecimalFormat;
import java.util.List;

public class TrommelCategory implements DisplayCategory<TrommelDisplay>
{
    @Override
    public CategoryIdentifier<? extends TrommelDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.TROMMEL;
    }

    @Override
    public Text getTitle()
    {
        return new TranslatableText("category." + NeepMeat.NAMESPACE + ".trommel");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(NMBlocks.SMALL_TROMMEL);
    }

    @Override
    public List<Widget> setupDisplay(TrommelDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);

        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 8)));

        // Input slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 9)).entries(display.getInputEntries().get(0)).markInput());

        // Output slot
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 9)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 9)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());

        // Auxiliary output slot
        if (display.getOutputEntries().size() > 1)
        {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 87, startPoint.y + 9)).entries(display.getOutputEntries().get(1)).markOutput());
        }
        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 55;
    }
}
