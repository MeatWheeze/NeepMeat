package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CyclingButtonWidget extends ButtonWidget
{

    protected final Identifier texture;
    protected final int u;
    protected final int v;
    protected final int tw;
    protected final int th;
    protected final int vOffset;
    protected final int maxIndex;

    protected int index;

    public CyclingButtonWidget(int x, int y, int width, int height, int u, int v, int vOffset, int maxIndex, Identifier texture, int tw, int th, Text message, PressAction onPress)
    {
        super(x, y, width, height, message, onPress);

        this.texture = texture;
        this.u = u;
        this.v = v;
        this.tw = tw;
        this.th = th;
        this.vOffset = vOffset;
        this.maxIndex = maxIndex;
        this.index = 0;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.enableDepthTest();
        TexturedButtonWidget.drawTexture(matrices, this.x, this.y, this.u, this.v + this.vOffset * this.index, this.width, this.height, this.tw, this.th);
        if (this.isHovered())
        {
            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public void cycle()
    {
        if (index < this.maxIndex)
        {
            ++index;
        }
        else
        {
            index = 0;
        }
    }
}
