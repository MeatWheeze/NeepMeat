package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.meatlib.item.TooltipSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
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
    protected final MousePressAction onPress;

    @Environment(value= EnvType.CLIENT)
    public CyclingButtonWidget(int x, int y, int width, int height, int u, int v, int vOffset, int maxIndex, Identifier texture, int tw, int th, Text message, MousePressAction onPress)
    {
        super(x, y, width, height, message, button -> { }, button -> { return null; });

        this.texture = texture;
        this.u = u;
        this.v = v;
        this.tw = tw;
        this.th = th;
        this.vOffset = vOffset;
        this.maxIndex = maxIndex;
        this.index = 0;

        this.onPress = onPress;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.enableDepthTest();
        TexturedButtonWidget.drawTexture(matrices, this.getX(), this.getY(), this.u, this.v + this.vOffset * this.index, this.width, this.height, this.tw, this.th);

        if (this.isHovered())
        {
//            super.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (!this.active || !this.visible)
        {
            return false;
        }
        if (this.isValidClickButton(button) && (this.clicked(mouseX, mouseY)))
        {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
//            this.onClick(mouseX, mouseY);
            this.onPress(button);
            return true;
        }
        return false;
    }

    public void onPress(int mouseButton)
    {
        this.onPress.press(this, mouseButton);
    }

    @Override
    protected boolean isValidClickButton(int button)
    {
        return button == 0 || button == 1;
    }

    public void renderTooltip(MatrixStack matrixStack, int x, int y)
    {
//        System.out.println("ooooooo");
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

    @Environment(value= EnvType.CLIENT)
    public interface MousePressAction
    {
        void press(ButtonWidget button, int mouseButton);
    }
}
