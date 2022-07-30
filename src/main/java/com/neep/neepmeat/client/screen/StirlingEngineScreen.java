package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.StirlingEngineScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StirlingEngineScreen extends HandledScreen<StirlingEngineScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/stirling_engine.png");

    private final StirlingEngineScreenHandler handler;

    public StirlingEngineScreen(StirlingEngineScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.handler = handler;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        int i = this.x;
        int j = this.y;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        drawBurnTime(matrices, i, j);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrices);
        drawBurnTime(matrices, this.x, this.y);
        super.render(matrices, mouseX, mouseY, delta);
        textRenderer.drawWithShadow(matrices, Integer.toString(handler.getProperty(0)), 0, 0, 65280);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    public void drawBurnTime(MatrixStack matrices, int i, int j)
    {
        int time = handler.getProperty(0);
        if (time < 1)
            return;

        int total = handler.getProperty(1);
        int k = (int) ((time / (total + 1f)) * 14);
        this.drawTexture(matrices, i + 81, j + 41 + 12 - k, 176, 12 - k, 14, k + 1);
    }

    @Override
    protected void init()
    {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
