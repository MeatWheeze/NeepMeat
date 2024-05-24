package com.neep.neepmeat.client.screen.plc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;

public abstract class PLCScreenButton extends ClickableWidget
{
    public PLCScreenButton(int x, int y, Text message)
    {
        super(x, y, 16, 16, message);
    }

    @Override
    public void renderButton(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, PLCProgramScreen.WIDGETS);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int thingHeight = 16;
        matrices.drawTexture(PLCProgramScreen.WIDGETS, getX(), getY(), 0, getU(), getV() + i * thingHeight, this.width, this.height, 256, 256);

        if (isMouseOver(mouseX, mouseY))
        {
            renderTooltip(matrices, mouseX, mouseY);
        }
    }

    protected int getYImage(boolean hovered)
    {
        return hovered ? 2 : 1;
    }

    @Override
    public void playDownSound(SoundManager soundManager)
    {
        soundManager.play(PositionedSoundInstance.master(NMSounds.PLC_SELECT, 1.0F));
        soundManager.play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0F));
    }

    protected int getU()
    {
        return 0;
    }

    protected int getV()
    {
        return 0;
    }

    abstract public void renderTooltip(DrawContext matrices, int mouseX, int mouseY);

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder)
    {

    }
}
