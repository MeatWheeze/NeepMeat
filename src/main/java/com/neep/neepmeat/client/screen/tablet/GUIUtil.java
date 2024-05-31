package com.neep.neepmeat.client.screen.tablet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

public interface GUIUtil
{
    Identifier INVENTORY_BACKGROUND = new Identifier(NeepMeat.NAMESPACE, "textures/gui/inventory_background.png");

    static void renderBorder(MatrixStack context, int x, int y, int dx, int dy, int col, int offset)
    {
        drawHorizontalLine1(context, x - offset, x + dx + offset, y - offset, col);
        drawVerticalLine1(context, x - offset, y - offset, y + dy + offset, col);
        drawHorizontalLine1(context, x - offset, x + dx + offset, y + dy + offset, col);
        drawVerticalLine1(context, x + dx + offset, y - offset, y + dy + offset, col);
    }

    static void drawCenteredText(MatrixStack context, TextRenderer textRenderer, Text text, float centerX, float y, int color, boolean shadow)
    {
//        OrderedText orderedText = text.asOrderedText();
        drawText(context, textRenderer, text, centerX - textRenderer.getWidth(text) / 2f, y, color, shadow);
    }


    static int drawText(MatrixStack context, TextRenderer textRenderer, Text text, float x, float y, int color, boolean shadow)
    {
        if (shadow)
            return textRenderer.drawWithShadow(context, text, x, y, color);
        else
            return textRenderer.draw(context, text, x, y, color);
    }

    static int drawText(MatrixStack context, TextRenderer textRenderer, OrderedText text, float x, float y, int color, boolean shadow)
    {
        if (shadow)
            return textRenderer.drawWithShadow(context, text, x, y, color);
        else
            return textRenderer.draw(context, text, x, y, color);
    }

    static int drawText(MatrixStack context, TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow)
    {
        if (shadow)
            return textRenderer.drawWithShadow(context, text, x, y, color);
        else
            return textRenderer.draw(context, text, x, y, color);
    }

    static void drawHorizontalLine1(MatrixStack matrices, int x1, int x2, int y, int color)
    {
        if (x2 < x1)
        {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        DrawableHelper.fill(matrices, x1, y, x2 + 1, y + 1, color);
    }

    static void drawVerticalLine1(MatrixStack matrices, int x, int y1, int y2, int color)
    {
        if (y2 < y1)
        {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        DrawableHelper.fill(matrices, x, y1 + 1, x + 1, y2, color);
    }

    static void drawInventoryBackground(MatrixStack context, int x, int y)
    {
        RenderSystem.setShaderTexture(0, INVENTORY_BACKGROUND);
        drawTexture(context, x, y, 0, 0, 176, 90, 256, 256);
    }

    static void drawNineSlicedTexture(MatrixStack matrices, Identifier texture, int x, int y, int width, int height, int leftSliceWidth, int topSliceHeight, int rightSliceWidth, int bottomSliceHeight, int centerSliceWidth, int centerSliceHeight, int u, int v)
    {

    }

    static void drawNineSlicedTexture(MatrixStack matrices, Identifier texture, int x, int y, int width, int height, int outerSliceSize, int centerSliceWidth, int centerSliceHeight, int u, int v)
    {
        drawNineSlicedTexture(matrices, texture, x, y, width, height, outerSliceSize, outerSliceSize, outerSliceSize, outerSliceSize, centerSliceWidth, centerSliceHeight, u, v);
    }

    static void drawNineSlicedTexture(MatrixStack matrices, Identifier texture, int x, int y, int width, int height, int outerSliceWidth, int outerSliceHeight, int centerSliceWidth, int centerSliceHeight, int u, int v)
    {
        drawNineSlicedTexture(matrices, texture, x, y, width, height, outerSliceWidth, outerSliceHeight, outerSliceWidth, outerSliceHeight, centerSliceWidth, centerSliceHeight, u, v);
    }
}