package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;

public class PLCStackViewer implements Drawable
{
    private int x;
    private int y;
    private int width;
    private int height;
    private final PLCBlockEntity plc;

    private int lastHeight;

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;
    public PLCStackViewer(PLCBlockEntity plc)
    {
        this.plc = plc;
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        var stack = plc.getVariableStack();

        int stride = textRenderer.fontHeight + 1;
        lastHeight = ((stack.size() + 2) * stride + 2);

        Text name = Text.literal("Stack");
        int nameWidth = textRenderer.getWidth(name);

        // Fit width to the widest entry
        int adjustedWidth = nameWidth + 2;
        for (int i = 0; i < stack.size(); ++i)
        {
            adjustedWidth = Math.max(adjustedWidth, 2 + textRenderer.getWidth(String.valueOf(stack.peekInt(i))));
        }

        int adjustedX = x - adjustedWidth + width;
        matrices.fill(adjustedX, y + height - lastHeight, x + width, y + height, 0x90000000);
        GUIUtil.renderBorder(matrices, adjustedX, y + height - lastHeight, adjustedWidth, lastHeight - 1, PLCCols.BORDER.col, 0);

        int textY = this.y + height - 1 - textRenderer.fontHeight;

        GUIUtil.drawText(matrices, textRenderer, "Stack", adjustedX + 1 + (adjustedWidth - nameWidth) / 2f, textY, PLCCols.TEXT.col, false);
        textY -= stride;
        for (int i = stack.size() - 1; i >= 0; --i)
        {
            int entry = stack.peekInt(i);
//            textRenderer.draw(matrices, Integer.toString(i, 10), x + 2, y + 2 + i * stride, PLCCols.BORDER.col);
            GUIUtil.drawText(matrices, textRenderer, Integer.toString(entry, 10), adjustedX + 2, textY, PLCCols.TEXT.col, true);
            textY -= stride;
        }
    }

    public void init(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
