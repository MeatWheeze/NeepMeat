package com.neep.neepmeat.client.screen.tablet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

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

    /**
     * Segments and stretches the defined region to draw it as a window border. Only works with plain colours, since the texture is stretched or squashed.
     */
    static void drawFiveSlicedTexture(MatrixStack matrices, Identifier texture, int x, int y, int width, int height, int sliceBorder, int regionWidth, int regionHeight, int u, int v)
    {
        RenderSystem.setShaderTexture(0, texture);
        Matrix4f pos = matrices.peek().getPositionMatrix();
        int z = 0;
        int centerSliceWidth = regionWidth - 2 * sliceBorder;

        // Top
        drawTexturedQuad(pos, x + sliceBorder, x + width - sliceBorder, y, y + sliceBorder, z, u + sliceBorder, u + sliceBorder + centerSliceWidth, v, v + sliceBorder);

        // Bottom
        drawTexturedQuad(pos, x + sliceBorder, x + width - sliceBorder, y + height - sliceBorder, y + height, z, u + sliceBorder, u + sliceBorder + centerSliceWidth, v + regionHeight - sliceBorder, v + regionHeight);

        // Left
        drawTexturedQuad(pos, x, x + sliceBorder, y + sliceBorder, y + height - sliceBorder, z, u, u + sliceBorder, v + sliceBorder, v + regionHeight - sliceBorder);

        // Right
        drawTexturedQuad(pos, x + width - sliceBorder, x + width, y + sliceBorder, y + height - sliceBorder, z, u + regionWidth - sliceBorder, u + regionWidth, v + sliceBorder, v + regionHeight - sliceBorder);

        // Middle
        drawTexturedQuad(pos, x + sliceBorder, x + width - sliceBorder, y + sliceBorder, y + height - sliceBorder, z, u + sliceBorder, u + regionWidth - sliceBorder, v + sliceBorder, v + regionHeight - sliceBorder);

        // Top left corner
        drawTexturedQuad(pos, x, x + sliceBorder, y, y + sliceBorder, z, u, u + sliceBorder, v, v + sliceBorder);

        // Bottom left corner
        drawTexturedQuad(pos, x, x + sliceBorder, y + height - sliceBorder, y + height, z, u, u + sliceBorder, v + regionHeight - sliceBorder, v + regionHeight);

        // Bottom right corner
        drawTexturedQuad(pos, x + width - sliceBorder, x + width, y + height - sliceBorder, y + height, z, u + regionWidth - sliceBorder, u + regionWidth, v + regionHeight - sliceBorder, v + regionHeight);

        // Top right corner
        drawTexturedQuad(pos, x + width - sliceBorder, x + width, y, y + sliceBorder, z, u + regionWidth - sliceBorder, u + regionWidth, v, v + sliceBorder);
    }

    private static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1)
    {
        u0 /= 256;
        u1 /= 256;
        v0 /= 256;
        v1 /= 256;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
    }
}