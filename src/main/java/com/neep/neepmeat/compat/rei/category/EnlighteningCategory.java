package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.data.DataUtil;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.EnlighteningDisplay;
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
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;
import java.util.List;

public class EnlighteningCategory implements DisplayCategory<EnlighteningDisplay>
{
    private final Identifier texture = new Identifier(NeepMeat.NAMESPACE, "textures/gui/enlightenment.png");

    @Override
    public CategoryIdentifier<? extends EnlighteningDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.ENLIGHTENING;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("category." + NeepMeat.NAMESPACE + ".enlightening");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(NMBlocks.PEDESTAL);
    }

    @Override
    public List<Widget> setupDisplay(EnlighteningDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);
//        DecimalFormat df = new DecimalFormat("###.##");
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 9)));

        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5),
                Text.translatable("category." + NeepMeat.NAMESPACE + ".enlightening.data",
                        DataUtil.formatData(display.getData()))).noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 25, startPoint.y + 9)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 3)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 9)).entries(display.getOutputEntries().get(0)).markOutput().disableBackground());

        int thingWidth = 44;
        widgets.add(Widgets.createTexturedWidget(texture, new Rectangle(startPoint.x - 24, startPoint.y - 4, thingWidth, thingWidth), 0, 0, thingWidth, thingWidth));

        if (display.getOutputEntries().size() > 1)
        {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 81, startPoint.y + 9)).entries(display.getOutputEntries().get(1)).markOutput());
        }
        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 55;
    }
}
