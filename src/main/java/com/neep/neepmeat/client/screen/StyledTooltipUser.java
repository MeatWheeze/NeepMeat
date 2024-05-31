package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.stream.Collectors;

public interface StyledTooltipUser
{
    TextRenderer textRenderer();

    int width();

    int height();

    default void renderTooltipText(MatrixStack matrices, List<Text> texts, boolean offset, int x, int y, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(t -> TooltipComponent.of(t.asOrderedText())).collect(Collectors.toList()), offset, x, y, 0, col);
    }

    default void renderTooltipOrderedText(MatrixStack matrices, List<OrderedText> texts, boolean offset, int x, int y, int width, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(TooltipComponent::of).collect(Collectors.toList()), offset, x, y, width, col);
    }

    default void renderTooltipComponents(MatrixStack matrices, List<TooltipComponent> components, boolean offset, int x, int y, int maxWidth, int col)
    {
        if (offset)
        {
            x += 12;
            y -= 12;
        }
        if (components.isEmpty())
        {
            return;
        }

        int maxHeight = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components)
        {
            int componentWidth = tooltipComponent.getWidth(textRenderer());
            if (componentWidth > maxWidth)
            {
                maxWidth = componentWidth;
            }
            maxHeight += tooltipComponent.getHeight();
        }

        if (x + maxWidth > width())
        {
            x -= 28 + maxWidth;
        }

        if (y + maxHeight + 6 > height())
        {
            y = height() - maxHeight - 6;
        }

        matrices.push();
//        this.itemRenderer.zOffset = 400.0f;
//        this.setZOffset(400);
        matrices.translate(0, 0, 400);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        DrawableHelper.fill(matrices, x, y, x + maxWidth + 2, y + maxHeight + 2, 0x90000000);
        GUIUtil.drawHorizontalLine1(matrices, x, x + maxWidth + 2, y, col);
        GUIUtil.drawHorizontalLine1(matrices, x, x + maxWidth + 2, y + maxHeight + 2, col);
        GUIUtil.drawVerticalLine1(matrices, x + maxWidth + 2, y, y + maxHeight + 2, col);
        GUIUtil.drawVerticalLine1(matrices, x, y, y + maxHeight + 2, col);

        RenderSystem.disableBlend();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        int yAdvance = y + 2;

        for (TooltipComponent tooltipComponent2 : components)
        {
            tooltipComponent2.drawText(textRenderer(), x + 2, yAdvance, matrix4f, immediate);
            yAdvance += tooltipComponent2.getHeight();
        }

        immediate.draw();
        matrices.pop();
        yAdvance = y;
        for (int index = 0; index < components.size(); ++index)
        {
            TooltipComponent tooltipComponent2 = components.get(index);
            tooltipComponent2.drawItems(textRenderer(), x, yAdvance, matrices, MinecraftClient.getInstance().getItemRenderer(), 400);
            yAdvance += tooltipComponent2.getHeight() + (index == 0 ? 2 : 0);
        }
    }
}
