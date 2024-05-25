package com.neep.neepmeat.client.screen.living_machine;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.machine.live_machine.Processes;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ProcessesPane extends LivingMachineScreen.PaneWidget
{
    private final LivingMachineScreen parent;

    private final List<EntryWidget> entries = new ArrayList<>();

    public ProcessesPane(LivingMachineScreen parent)
    {
        this.parent = parent;
    }

    @Override
    protected void init()
    {
        addDrawableChild(new MetricsPane.ThingyButton(bounds.x() + 2, bounds.y() + bounds.h() - 16 - 2, 112, parent::switchMode));

        entries.clear();
        for (var pair : Processes.getInstance().getEntries())
        {
            entries.add(new EntryWidget(pair, bounds.w(), textRenderer.fontHeight + 3));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

        int yOffset = bounds.y() + textRenderer.fontHeight + 4;
        for (var entry : entries)
        {
            yOffset += entry.render(context, bounds.x() + 2, yOffset, mouseX, mouseY, delta) + 1;
        }

        GUIUtil.drawCenteredText(context, textRenderer, Text.of("Processes and Requirements"), bounds.x() + bounds.w() / 2f, bounds.y() + 3, PLCCols.TEXT.col, false);
    }

    private class EntryWidget
    {
        private final int w;
        private final Process process;
        private final int h;
        private int prevX;
        private int prevY;

//        private List<OrderedText> texts;

        public EntryWidget(Pair<BitSet, Process> pair, int w, int h)
        {
            this.w = w;
            this.process = pair.value();
            this.h = h;

//            this.texts = new ArrayList<>();
        }

        private boolean isMouseOver(double mouseX, double mouseY)
        {
            return prevX <= mouseX && prevY <= mouseY && prevX + w >= mouseX && prevY + h >= mouseY;
        }

        public float render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta)
        {
            this.prevX = x;
            this.prevY = y;

            GUIUtil.drawHorizontalLine1(context, x + 1, x + w - 6, y, PLCCols.TRANSPARENT.col);
            GUIUtil.drawText(context, textRenderer, process.getName(), x + 1, y + 3, PLCCols.TEXT.col, false);

            if (isMouseOver(mouseX, mouseY))
            {
                int width = 180;
                List<OrderedText> texts = new ArrayList<>();
                Text head = NeepMeat.translationKey("screen", "living_machine.requirements");
                texts.add(head.asOrderedText());
                List<ComponentType<?>> requiredTypes = process.getRequired();
                for (var type : requiredTypes)
                {
                    Identifier id = ComponentType.REGISTRY.getId(type);
                    if (id == null)
                        continue;

                    Formatting formatting = parent.getScreenHandler().getBlockEntity().getDisplayComponents().contains(id) ?
                            Formatting.GOLD
                            : Formatting.GRAY;
                    texts.addAll(textRenderer.wrapLines(
                            Text.literal(" ")
                                    .append(Text.translatable(id.toTranslationKey("component")).formatted(formatting)), width));
                }

                parent.renderTooltipOrderedText(context, texts, false, x + w - 3, y, width, PLCCols.TEXT.col);
            }

            return h;
        }
    }
}
