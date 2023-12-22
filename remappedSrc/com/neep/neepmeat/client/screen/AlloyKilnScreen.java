package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.AlloyKilnScreenHandler;
import com.neep.neepmeat.screen_handler.StirlingEngineScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class AlloyKilnScreen extends HandledScreen<AlloyKilnScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/alloy_kiln.png");

    private final AlloyKilnScreenHandler handler;

    public AlloyKilnScreen(AlloyKilnScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.handler = handler;
        backgroundWidth = 176;
        backgroundHeight = 176;
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
        drawProgress(matrices, i, j);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    public void drawBurnTime(MatrixStack matrices, int i, int j)
    {
        int time = handler.getProperty(0);
        if (time < 1)
            return;

        int total = handler.getProperty(1);
        int k = (int) ((time / (total + 1f)) * 14);
        this.drawTexture(matrices, i + 57, j + 37 + 12 - k, 176, 12 - k, 14, k + 1);
    }

    public void drawProgress(MatrixStack matrices, int i, int j)
    {
        int time = handler.getProperty(2);
        if (time < 1)
            return;

        int total = handler.getProperty(3);
        int k = (int) ((time / (total + 1f)) * 24);
        this.drawTexture(matrices, i + 80, j + 36, 176, 14, k, 17);
    }

    @Override
    protected void init()
    {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
