package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CurrentArgumentWidget implements Drawable
{
    private final int x;
    private final int y;
    private final PLCScreenHandler handler;
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public CurrentArgumentWidget(int x, int y, PLCScreenHandler handler)
    {
        this.x = x;
        this.y = y;

        this.handler = handler;
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        Text text = Text.translatable("text." + NeepMeat.NAMESPACE + ".plc.arguments", handler.getArguments(), handler.getMaxArguments());

        matrices.drawText(textRenderer, text, x, y, PLCCols.TEXT.col, true);
    }
}
