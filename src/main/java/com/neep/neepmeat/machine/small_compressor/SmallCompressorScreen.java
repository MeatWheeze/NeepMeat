package com.neep.neepmeat.machine.small_compressor;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SmallCompressorScreen extends HandledScreen<SmallCompressorScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/small_compressor.png");

    public SmallCompressorScreen(SmallCompressorScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 176;
        this.backgroundHeight = 90 + 1 + 90;
        super.init();
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta)
    {
        renderBackground(context);

        super.render(context, mouseX, mouseY, delta);

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        GUIUtil.drawTexture(TEXTURE, matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int burnTime = handler.getProperty(0);
        int maxBurnTime = handler.getProperty(1);
        if (burnTime >= 0 && maxBurnTime > 0)
        {
            float f = (float) burnTime / maxBurnTime;
            int height = (int) (f * 14);
            GUIUtil.drawTexture(TEXTURE, matrices,  i + 56 + 24, j + 36 + 14 - height, 176, 14 - height, 14, height);
        }

        GUIUtil.drawInventoryBackground(matrices, i, j + 91);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
    }
}
