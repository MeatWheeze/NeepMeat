package com.neep.neepmeat.client.screen.living_machine;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.screen_handler.LivingMachineScreenHandler;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MetricsPane extends LivingMachineScreen.PaneWidget
{
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private final LivingMachineScreenHandler handler;
    private final List<Pair<Text, Supplier<String>>> entries = new ArrayList<>();

    private final DecimalFormat floatFormat = new DecimalFormat("####.#");
    private final DecimalFormat smallFloatFormat = new DecimalFormat("####.####");
    private final DecimalFormat intFormat = new DecimalFormat("####");

    public MetricsPane(LivingMachineScreenHandler handler)
    {
        this.handler = handler;

        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.rated_power"),
                () -> PowerUtils.perUnitToText(handler.getBlockEntity().getRatedPower()).getString()));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.power"),
                () -> PowerUtils.perUnitToText(handler.getBlockEntity().getPower()).getString()));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.efficiency"),
                () -> intFormat.format(100 * handler.getBlockEntity().getEfficiency()) + "%"));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.degradation_rate"),
                () -> formatRepair(handler.getBlockEntity().getCurrentDegradationRate())));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.self_repair"),
                () -> formatRepair(handler.getBlockEntity().getSelfRepair())));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.rul"),
                () -> formatRUL(handler.getBlockEntity().getRulSecs())));
        entries.add(Pair.of(Text.empty(), () -> ""));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.size"),
                () -> intFormat.format(handler.getBlockEntity().getNumStructures())));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.components"),
                () -> intFormat.format(handler.getBlockEntity().getNumComponents())));
        entries.add(Pair.of(NeepMeat.translationKey("screen", "living_machine.process"),
                () -> handler.getBlockEntity().getProcess().getString()));
    }

    public void tick()
    {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

        int yStride = textRenderer.fontHeight + 2;

        int padding = 3;
        int yOffset = bounds.y() + padding;
        int xOffset = bounds.x() + padding;
        int xRight = bounds.x() + bounds.w() - padding;
        int textCol = PLCCols.TEXT.col;
        int numberCol = PLCCols.SELECTED.col;
        for (var entry : entries)
        {
            GUIUtil.drawText(context, textRenderer, entry.first(), xOffset, yOffset, textCol, false);
            String number = entry.second().get();
            int numberWidth = textRenderer.getWidth(number);

            GUIUtil.drawText(context, textRenderer, number, xRight - numberWidth, yOffset, numberCol, false);

            yOffset += yStride;
        }
    }

    private static String formatRUL(long secs)
    {
        if (secs == -1)
            return "infinite";

        Duration duration = Duration.ofSeconds(secs);
        if (duration.toHoursPart() > 0)
        {
            return duration.toHoursPart() + "hr " + duration.toMinutesPart() + "m";
        }
        else if (duration.toMinutesPart() > 0)
        {
            return duration.toMinutesPart() + "m " + duration.toSecondsPart() + "s";
        }
        else
        {
            return duration.toSecondsPart() + "s";
        }
    }

    private String formatRepair(float repair)
    {
        return smallFloatFormat.format(repair * 20 * 100) + "%/s";
    }
}
