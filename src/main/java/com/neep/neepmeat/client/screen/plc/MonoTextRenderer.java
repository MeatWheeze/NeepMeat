package com.neep.neepmeat.client.screen.plc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MonoTextRenderer
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/font/ascii-mono.png");

    private final TextHandler handler = new TextHandler((codePoint, style) -> charStride());

    public void drawTexture(Matrix4f matrix, float x, float y, float u, float v, float width, float height)
    {
        drawTexture(matrix, x, y, 0, u, v, width, height, 128, 128);
    }

    public static void drawTexture(Matrix4f matrix, float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight)
    {
        drawTexture(matrix, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }

    private static void drawTexture(Matrix4f matrix, float x0, float x1, float y0, float y1, float z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight)
    {
        drawTexturedQuad(matrix, x0, x1, y0, y1, z, (u + 0.0F) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight, (v + regionHeight) / textureHeight);
    }

    private static void drawTexturedQuad(Matrix4f matrix, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1)
    {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, x1, y0, z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, x0, y0, z).texture(u0, v0).next();
    }

    public int fontHeight()
    {
        return 9;
    }

    public float draw(MatrixStack matrices, @Nullable String text, float x, float y, int color)
    {
        if (text == null || text.isEmpty())
            return 0;

        return this.drawLayer(text, x, y, color, matrices.peek().getPositionMatrix());
    }

    public float draw(MatrixStack matrices, OrderedText text, float x, float y, int color)
    {
        return this.drawLayer(text, x, y, color, matrices.peek().getPositionMatrix());
    }

    private float drawLayer(String text, float x, float y, int color, Matrix4f matrix)
    {
        RenderSystem.enableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        Drawer drawer = new Drawer(x, y, color, matrix);
        TextVisitFactory.visitFormatted(text, Style.EMPTY, drawer);
        return drawer.draw();
    }

    private float drawLayer(OrderedText text, float x, float y, int color, Matrix4f matrix)
    {
        RenderSystem.enableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        Drawer drawer = new Drawer(x, y, color, matrix);
        text.accept(drawer);
        return drawer.draw();
    }

    private int getU(int codePoint)
    {
        int rows = 16;
        int dx = 8;

        return codePoint % rows * dx;
    }

    private int getV(int codePoint)
    {
        int cols = 16;
        int dy = 8;

        return (codePoint / cols) * dy;
    }

    public float drawWithShadow(MatrixStack matrices, String text, float x, float y, int color)
    {
        return this.draw(matrices, text, x, y, color);
    }

    public List<OrderedText> wrapLines(StringVisitable text, int width)
    {
        return Language.getInstance().reorder(this.handler.wrapLines(text, width, Style.EMPTY));
    }

    public void drawGlyph(int codePoint, float x, float y, Matrix4f matrix, BufferBuilder builder, boolean italic, float red, float green, float blue, float alpha, int light)
    {
        float x0 = x + 0;
        float x1 = x0 + charWidth();
        float y0 = y - 0;
        float y1 = y0 + charHeight();
        float h = 0 - 3.0F;
        float j = charHeight() - 3.0F;

        float u1 = getU(codePoint) / 128f;
        float v1 = getV(codePoint) / 128f;
        float u2 = (getU(codePoint) + charWidth()) / 128f;
        float v2 = (getV(codePoint) + charHeight()) / 128f;

        float m = italic ? 1.0F - 0.25F * h : 0.0F;
        float n = italic ? 1.0F - 0.25F * j : 0.0F;

        builder.vertex(matrix, x0, y1, 0).color(red, green, blue, alpha).texture(u1, v2).next();
        builder.vertex(matrix, x1, y1, 0).color(red, green, blue, alpha).texture(u2, v2).next();
        builder.vertex(matrix, x1, y0, 0).color(red, green, blue, alpha).texture(u2, v1).next();
        builder.vertex(matrix, x0, y0, 0).color(red, green, blue, alpha).texture(u1, v1).next();
    }

    public int getWidth(String text)
    {
        return text.length() * charStride();
//        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int charWidth()
    {
        return 5;
    }

    public int charStride()
    {
        return charWidth() + 1;
    }

    public int charHeight()
    {
        return 8;
    }

    public TextHandler getTextHandler()
    {
        return handler;
    }

    public String trimToWidth(String text, int width)
    {
        return this.handler.trimToWidth(text, width, Style.EMPTY);
    }

    private class Drawer implements CharacterVisitor
    {
        private final Matrix4f matrix;
        private final float r;
        private final float g;
        private final float b;
        private final float a;
        private final BufferBuilder bufferBuilder;
        private float x;
        private float y;

        public Drawer(float x, float y, int col, Matrix4f matrix)
        {
            this.bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

            this.x = x;
            this.y = y;
            this.matrix = matrix;

            this.r = (col >> 16 & 255) / 255.0F;
            this.g = (col >> 8 & 255) / 255.0F;
            this.b = (col & 255) / 255.0F;
            this.a = (col >> 24 & 255) / 255.0F;
        }

        @Override
        public boolean accept(int index, Style style, int codePoint)
        {
            int width = charStride();

            drawGlyph(codePoint, x, y, matrix, bufferBuilder, false, r, g, b, a, 15728880);
            this.x += width;
            return true;
        }

        public float draw()
        {
            BufferRenderer.drawWithShader(bufferBuilder.end());
            return x + 1;
        }
    }
}
