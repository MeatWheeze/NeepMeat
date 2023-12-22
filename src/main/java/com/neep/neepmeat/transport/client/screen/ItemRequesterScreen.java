package com.neep.neepmeat.transport.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.FluidRationerScreenHandler;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class ItemRequesterScreen extends HandledScreen<ItemRequesterScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/item_requester.png");


    public ItemRequesterScreen(ItemRequesterScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 231;
    }

    @Override
    protected void init()
    {
        super.init();

        this.titleX = 29;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
//        this.textRenderer.draw(matrices, this.title, this.playerInventoryTitleX, this.titleY, 0x404040);
    }
}