package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.button.TextToggleWidget;
import com.neep.neepmeat.screen_handler.AssemblerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class AssemblerScreen extends HandledScreen<AssemblerScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/assembler.png");

    private final AssemblerScreenHandler handler;

    public AssemblerScreen(AssemblerScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.handler = handler;
        backgroundWidth = 216;
        backgroundHeight = 211;
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

        // Indicate disabled slots
        renderCrosses(matrices, x + 7, y + 17);
//        renderCrosses(matrices, x + 66, y + 17);
        renderOutputOverlay(matrices, x + 7, y + 17);
        renderOutputOverlay(matrices, x + 66, y + 17);
    }

    public void renderCrosses(MatrixStack matrices, int startX, int startY)
    {
        int nx = 3;
        int ny = 4;
        for (int l = 0; l < ny; ++l)
        {
            for (int k = 0; k < nx; ++k)
            {
                // Draw red X if slot index is larger than the target inventiry
                if (k + l * nx >= handler.getProperty(1))
                {
                    drawTexture(matrices, startX + k * 18, startY + l * 18, 233, 0, 18, 18);
                }
            }
        }
    }

    public void renderOutputOverlay(MatrixStack matrices, int startX, int startY)
    {
        int nx = 3;
        int ny = 4;
        for (int l = 0; l < ny; ++l)
        {
            for (int k = 0; k < nx; ++k)
            {
                int index = k + l * nx;
                // Shift bit corresponding to the slot index to the right
                if (((handler.getProperty(0) >> index) & 1) == 1)
                    drawTexture(matrices, startX + k * 18, startY + l * 18, 216, 0, 18, 18);
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
        this.textRenderer.draw(matrices, Text.translatable("container.neepmeat.assembler.display"), this.titleX, this.titleY, 0x404040);
//        this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 0x404040);
    }

    @Override
    protected void init()
    {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.addDrawableChild(new TextToggleWidget(this.x + 7, this.y + 93, 110, 20, Text.translatable("button." + NeepMeat.NAMESPACE + ".assembler.select"),
                handler.getProperty(2) > 0, (b, t) ->
        {

            buttonPress(AssemblerScreenHandler.ID_TOGGLE_SELECT);
        }));
    }

    public void buttonPress(int id)
    {
        this.client.interactionManager.clickButton(this.handler.syncId, id);
    }
}
