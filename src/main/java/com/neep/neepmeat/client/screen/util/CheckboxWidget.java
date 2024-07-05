package com.neep.neepmeat.client.screen.util;

import com.neep.neepmeat.api.plc.PLCCols;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

public class CheckboxWidget extends ClickableWidget
{
    private final TextRenderer textRenderer = client.textRenderer;
    private final BooleanSupplier toggled;
    private final ToggleAction onToggle;

    public CheckboxWidget(int x, int y, int w, int h, BooleanSupplier toggled, Text message, ToggleAction onToggle)
    {
        super(x, y, w, h, message);
        this.toggled = toggled;
        this.onToggle = onToggle;
    }

    public boolean borderActive(int mouseX, int mouseY)
    {
        return isMouseOver(mouseX, mouseY);
    }

    public boolean isToggled()
    {
        return toggled.getAsBoolean();
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
    {
        String box = isToggled() ? "☒" : "☐";
        int borderCol = borderActive(mouseX, mouseY) ? PLCCols.SELECTED.col : PLCCols.BORDER.col;

        int boxWidth = textRenderer.getWidth(box);
        int boxHeight = textRenderer.fontHeight;

        int textX = x() + (h() - boxWidth) / 2;
        int textY = y() + (h() - boxHeight) / 2;

        GUIUtil.drawText(context, textRenderer, Text.of(box), textX, textY, borderCol, false);
        GUIUtil.drawText(context, textRenderer, getMessage(), textX + boxWidth + 3, textY, PLCCols.TEXT.col, false);
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        super.onClick(mouseX, mouseY);
        boolean prevToggled = isToggled();
        onToggle.toggle(this, !prevToggled);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder)
    {

    }

    @FunctionalInterface
    public interface ToggleAction
    {
        void toggle(CheckboxWidget button, boolean toggled);
    }
}
