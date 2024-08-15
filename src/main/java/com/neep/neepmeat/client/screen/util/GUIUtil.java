package com.neep.neepmeat.client.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import software.bernie.geckolib.core.object.Color;

public interface GUIUtil
{
    Identifier INVENTORY_BACKGROUND = new Identifier(NeepMeat.NAMESPACE, "textures/gui/inventory_background.png");
    Identifier SCREEN_BORDER = new Identifier(NeepMeat.NAMESPACE, "textures/gui/screen_border.png");

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int u, int v, int width, int height)
    {
        drawTexture(texture, context, x, y, 0, (float) u, (float) v, width, height, 256, 256, 1, 1, 1, 1);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int u, int v, int width, int height, int col)
    {
        Color color = new Color(col);
        drawTexture(texture, context, x, y, 0, (float) u, (float) v, width, height, 256, 256, color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int u, int v, int width, int height, float r, float g, float b, float a)
    {
        drawTexture(texture, context, x, y, 0, (float) u, (float) v, width, height, 256, 256, r, g, b, a);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight)
    {
        drawTexture(texture, context, x, x + width, y, y + height, 0, width, height, u, v, textureWidth, textureHeight, 1, 1, 1, 1);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight, float r, float g, float b, float a)
    {
        drawTexture(texture, context, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight, r, g, b, a);
    }

    static void drawTextureStretch(Identifier texture, DrawContext context, int x1, int y1, int w, int h, float u, float v, int du, int dv, int textureWidth, int textureHeight)
    {
        drawTexturedQuad(texture, context, x1, x1 + w, y1, y1 + h, 0, u / textureWidth, (u + du) / textureWidth, v / textureHeight, (v + dv) / textureHeight, 1, 1, 1, 1);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x1, int x2, int y1, int y2, int z, int du, int dv, float u, float v, int textureWidth, int textureHeight, float r, float g, float b, float a)
    {
        drawTexturedQuad(texture, context, x1, x2, y1, y2, z, u / textureWidth, (u + du) / textureWidth, v / textureHeight, (v + dv) / textureHeight, r, g, b, a);
    }

    private static void drawTexturedQuad(Identifier texture, DrawContext context, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, float r, float g, float b, float a)
    {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).color(r, g, b, a).texture(u1, v1).next();
        bufferBuilder.vertex(matrix4f, x1, y2, z).color(r, g, b, a).texture(u1, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y2, z).color(r, g, b, a).texture(u2, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y1, z).color(r, g, b, a).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    static void renderBorder(DrawContext context, int x, int y, int dx, int dy, int col, int offset)
    {
        drawHorizontalLine1(context, x - offset, x + dx + offset, y - offset, col);
        drawVerticalLine1(context, x - offset, y - offset, y + dy + offset, col);
        drawHorizontalLine1(context, x - offset, x + dx + offset, y + dy + offset, col);
        drawVerticalLine1(context, x + dx + offset, y - offset, y + dy + offset, col);
    }

    static void renderBorderInner(DrawContext context, int x, int y, int dx, int dy, int col, int offset)
    {
        renderBorder(context, x, y, dx - 1, dy - 1, col, offset);
    }

    static void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, float centerX, float y, int color, boolean shadow)
    {
//        OrderedText orderedText = text.asOrderedText();
        drawText(context, textRenderer, text, centerX - textRenderer.getWidth(text) / 2f, y, color, shadow);
    }


    static int drawText(DrawContext context, TextRenderer textRenderer, Text text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw(text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
        return i;
    }

    static int drawText(DrawContext context, TextRenderer textRenderer, OrderedText text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw(text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
        return i;
    }

    static int drawText(DrawContext context, TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw(text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
        return i;
    }

    static void drawHorizontalLine1(DrawContext context, int x1, int x2, int y, int color)
    {
        if (x2 < x1)
        {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        context.fill(x1, y, x2 + 1, y + 1, color);
    }

    static void drawVerticalLine1(DrawContext context, int x, int y1, int y2, int color)
    {
        if (y2 < y1)
        {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        context.fill(x, y1 + 1, x + 1, y2, color);
    }

    static void drawInventoryBackground(DrawContext context, int x, int y)
    {
        context.drawTexture(INVENTORY_BACKGROUND, x, y, 0, 0, 176, 90);
    }

    static void drawScreenBorder(DrawContext context, int x, int y, int w, int h)
    {
        drawNineSlicedTexture(context, SCREEN_BORDER, x, y, w, h, 5, 255, 55, 0, 0);
    }

    /**
     * Segments and (sometimes) stretches the defined region to draw it as a window border. Stretching occurs when the desired size exceeds that of the image segment.
     */
    static void drawNineSlicedTexture(DrawContext matrices, Identifier texture, int x, int y, int width, int height, int sliceBorder, int regionWidth, int regionHeight, int u, int v)
    {
        RenderSystem.setShaderTexture(0, texture);
        Matrix4f pos = matrices.getMatrices().peek().getPositionMatrix();
        int z = 0;
        int centerSliceWidth = regionWidth - 2 * sliceBorder;

        int topAndBottomU1 = u + sliceBorder + Math.min(centerSliceWidth, width - 2 * sliceBorder);

        // Top
        drawTexturedQuad(pos, x + sliceBorder, x + width - sliceBorder, y, y + sliceBorder, z,
                u + sliceBorder, topAndBottomU1,
                v, v + sliceBorder);

        // Bottom
        drawTexturedQuad(pos, x + sliceBorder, x + width - sliceBorder, y + height - sliceBorder, y + height, z,
                u + sliceBorder, topAndBottomU1,
                v + regionHeight - sliceBorder, v + regionHeight);

        int leftRightV1 = v + sliceBorder + Math.min(width - 2 * sliceBorder, regionHeight - 2 * sliceBorder);

        // Left
        drawTexturedQuad(pos, x, x + sliceBorder, y + sliceBorder, y + height - sliceBorder, z,
                u, u + sliceBorder,
                v + sliceBorder, leftRightV1);

        // Right
        drawTexturedQuad(pos, x + width - sliceBorder, x + width, y + sliceBorder, y + height - sliceBorder, z,
                u + regionWidth - sliceBorder, u + regionWidth,
                v + sliceBorder, leftRightV1);

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

    static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1)
    {
        u0 /= 256;
        u1 /= 256;
        v0 /= 256;
        v1 /= 256;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    static void fill(/*$ drawContext >>*/ DrawContext context, int x, int y, int x2, int y2, int col)
    {
        context.fill(x, y, x2, y2, col);
    }
}
