package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.AlloyKilnScreenHandler;
import com.neep.neepmeat.screen_handler.AssemblerScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AssemblerScreen extends HandledScreen<AssemblerScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/assembler.png");

    private final AssemblerScreenHandler handler;

    public AssemblerScreen(AssemblerScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.handler = handler;
        backgroundWidth = 216;
        backgroundHeight = 203;
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

        renderCrosses(matrices, x + 7, y + 17);
        renderCrosses(matrices, x + 66, y + 17);
    }

    public void renderCrosses(MatrixStack matrices, int startX, int startY)
    {
        int nx = 3;
        int ny = 4;
        for (int l = 0; l < ny; ++l)
        {
            for (int k = 0; k < nx; ++k)
            {
                int oo = handler.getProperty(1);
                if (k + l * nx >= handler.getProperty(1))
                {
                    drawTexture(matrices, startX + k * 18, startY + l * 18, 233, 0, 18, 18);
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
//        this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 0x404040);
//        this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 0x404040);
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
