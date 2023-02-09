package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.SurgeryDisplay;
import com.neep.neepmeat.init.NMBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

public class TransformingToolCategory implements DisplayCategory<SurgeryDisplay>
{

    public static final Identifier GHOST_AXE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/ghost_axe.png");
    public static final Identifier GHOST_SWORD = new Identifier(NeepMeat.NAMESPACE, "textures/gui/ghost_sword.png");

    @Override
    public CategoryIdentifier<? extends SurgeryDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.TRANSFORMING_TOOL;
    }

    @Override
    public Text getTitle()
    {
        return new TranslatableText("category." + NeepMeat.NAMESPACE + ".transforming_tool");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(NMBlocks.SURGERY_CONTROLLER);
    }

    @Override
    public List<Widget> setupDisplay(SurgeryDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
        List<InputIngredient<EntryStack<?>>> input = display.getInputIngredients(3, 3);
        List<Slot> slots = Lists.newArrayList();
        int wSlot = 18;
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                if (y == 1 && (x == 0 || x == 2)) continue;

                Slot slot = Widgets.createSlot(new Point(startPoint.x + 1 + x * wSlot, startPoint.y + 1 + y * wSlot)).markInput();
                slot.entries(input.get(y * 3 + x).get());
                slots.add(slot);
            }
        }

        // Add desaturated items in slots 3 and 5.
        // Textured widgets available in Widgets do not seem to render, so I am using vanilla buttons.
        widgets.add(Widgets.wrapVanillaWidget(new TexturedButtonWidget(startPoint.x + 1, startPoint.y + 1 + wSlot, 16, 16, 0, 0, 0, GHOST_AXE, 16, 16, b -> {})));
        widgets.add(Widgets.wrapVanillaWidget(new TexturedButtonWidget(startPoint.x + 1 + 2 * wSlot, startPoint.y + 1 + wSlot, 16, 16, 0, 0, 0, GHOST_SWORD, 16, 16, b -> {})));
//        widgets.add(Widgets.createTexturedWidget(GHOST_AXE, new Rectangle(startPoint.x + 1, startPoint.y + 1 + wSlot, 16, 16)));
//        widgets.add(Widgets.createTexturedWidget(GHOST_SWORD, new Rectangle(startPoint.x + 1 + 2 * wSlot, startPoint.y + 1 + wSlot, 16, 16)));

        widgets.addAll(slots);
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }
}
