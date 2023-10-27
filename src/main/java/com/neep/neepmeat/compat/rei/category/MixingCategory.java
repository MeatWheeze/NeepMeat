package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.MixingDisplay;
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
import net.minecraft.text.TranslatableTextContent;
import java.text.DecimalFormat;
import java.util.List;

public class MixingCategory implements DisplayCategory<MixingDisplay>
{
    @Override
    public CategoryIdentifier<? extends MixingDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.MIXING;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("category." + NeepMeat.NAMESPACE + ".mixing");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(NMBlocks.MIXER);
    }

    @Override
    public List<Widget> setupDisplay(MixingDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);

        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));

        int processTime = display.getProcessTime();
        DecimalFormat df = new DecimalFormat("###.##");
        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5),
                Text.translatable("category." + NeepMeat.NAMESPACE + ".mixing.time", df.format(processTime / 20d))).noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 8)).animationDurationTicks(processTime));

        // Input slots
        if (!display.getRecipe().getFluidInputs().get(1).isEmpty())
        {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y - 1)).entries(display.getInputEntries().get(0)).markInput());
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 19)).entries(display.getInputEntries().get(1)).markInput());
        }
        else
        {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 9)).entries(display.getInputEntries().get(0)).markInput());
        }

        if (display.getRecipe().getItemIngredient().amount() != 0)
        {
            widgets.add(Widgets.createSlot(new Point(startPoint.x - 20, startPoint.y + 9)).entries(display.getInputEntries().get(2)).markInput());
        }

        // Output slot
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 9)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 9)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 55;
    }
}
