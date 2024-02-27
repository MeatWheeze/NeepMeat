package com.neep.neepmeat.guide.article;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.client.screen.plc.MonoTextRenderer;
import com.neep.neepmeat.client.screen.tablet.ArticleTextWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ImageContent implements Article.Content
{
//    private final Text caption;
    private final int width;
    private final int height;
    private final float scale;
    private final Identifier image;

    public ImageContent(int width, int height, float scale, Identifier image)
    {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.image = image;
    }

    @Override
    public int render(DrawContext context, float x, float y, float width, double scroll, ArticleTextWidget parent)
    {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        RenderSystem.setShaderTexture(0, image);

        float scaledWidth = width * scale;
        float scaledHeight = this.height * scaledWidth / this.width;

        float cx = x + (width / 2 - scaledWidth / 2f);
        float i = Math.max(0, parent.getTop() - y);
        float j = Math.max(0, y + scaledHeight - parent.getBottom());
        context.drawTexture(image,
                (int) cx,
                (int) ((int) y - scroll + i),
                0,
                0, i,
                (int) scaledWidth,
                (int) (scaledHeight - i - j),
                (int) scaledWidth, (int) scaledHeight);
        matrices.pop();

        return (int) scaledHeight;
    }

//    public static void drawTexture(MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight)
//    {
//        drawTexture(matrices, x, x + width, y, y + height, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
//    }
//
//    private static void drawTexture(MatrixStack matrices, int x0, int x1, int y0, int y1, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight)
//    {
//        drawTexturedQuad(matrices.peek().getPositionMatrix(), x0, x1, y0, y1, 0, (u + 0.0f) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0f) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
//    }
//
//    private static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1)
//    {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
//        bufferBuilder.vertex(matrix, x0, y1, z).texture(u0, v1).next();
//        bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).next();
//        bufferBuilder.vertex(matrix, x1, y0, z).texture(u1, v0).next();
//        bufferBuilder.vertex(matrix, x0, y0, z).texture(u0, v0).next();
//        bufferBuilder.end();
//        BufferRenderer.draw(bufferBuilder);
//    }
}
