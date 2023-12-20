/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020, 2021, 2022 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.VivisectionDisplay;
import com.neep.neepmeat.init.NMItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class VivisectionCategory implements DisplayCategory<VivisectionDisplay>
{
    @Override
    public CategoryIdentifier<? extends VivisectionDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.VIVISECTION;
    }
    
    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(NMItems.SACRIFICIAL_SCALPEL);
    }
    
    @Override
    public Text getTitle()
    {
        return Text.translatable("category." + NeepMeat.NAMESPACE + ".vivisection");
    }

    @Override
    public List<Widget> setupDisplay(VivisectionDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));

        if (!display.getEntities().isEmpty())
        {
            widgets.add(Widgets.createLabel(new Point(bounds.x + 35, bounds.y + 5), display.getEntities().get(0)));
        }
        else if (!display.getInputEntries().isEmpty())
        {
            widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y + 9)).markInput().backgroundEnabled(true)
                    .entries(display.getInputEntries().get(0)));
        }

        widgets.add(Widgets.createArrow(new Point(startPoint.x + 25, startPoint.y + 9)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 9)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 9))
                .entries(display.getOutputEntries().get(0)).markOutput().disableBackground());
        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 55;
    }
}