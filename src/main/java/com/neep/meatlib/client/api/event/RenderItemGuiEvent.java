package com.neep.meatlib.client.api.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.item.ItemStack;

// CURRENTLY BROKEN
public interface RenderItemGuiEvent
{
    Event<RenderItemGuiEvent> EVENT = EventFactory.createArrayBacked(RenderItemGuiEvent.class,
            (listeners) -> (context, textRenderer, stack, x, y) ->
            {
                for (RenderItemGuiEvent listener : listeners)
                {
                    listener.render(context, textRenderer, stack, x, y);
                }
            });

    void render(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y);

    static void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha)
    {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x + 0, y + 0, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + 0, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + 0, 0.0).color(red, green, blue, alpha).next();
        var built = buffer.end();
        BufferRenderer.drawWithGlobalProgram(built);
    }
}
