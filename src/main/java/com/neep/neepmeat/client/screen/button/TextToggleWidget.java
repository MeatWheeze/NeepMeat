package com.neep.neepmeat.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TextToggleWidget extends ButtonWidget
{
    public static final Tooltip EMPTY = Tooltip.of(Text.empty());

    protected final Tooltip tooltipSupplier;
    protected boolean toggled;
    protected ToggleAction onToggle;

    public TextToggleWidget(int x, int y, int width, int height, Text message, boolean toggled, ToggleAction onToggle)
    {
        this(x, y, width, height, message, toggled, onToggle, EMPTY);
    }

    public TextToggleWidget(int x, int y, int width, int height, Text message, boolean toggled, ToggleAction onToggle, Tooltip tooltipSupplier)
    {
        super(x, y, width, height, message, b -> {}, b -> null);
        this.toggled = toggled;
        this.onToggle = onToggle;
        this.tooltipSupplier = tooltipSupplier;
    }

    public void setToggled(boolean toggled)
    {
        this.toggled = toggled;
    }

    public boolean isToggled()
    {
        return this.toggled;
    }

    @Override
    public void onPress()
    {
        this.toggled = !this.toggled;
        this.onToggle.onToggle(this, toggled);
        super.onPress();
    }

//    @Override
//    public void appendNarrations(NarrationMessageBuilder builder)
//    {
//        this.appendDefaultNarrations(builder);
//        this.tooltipSupplier.supply(text -> builder.put(NarrationPart.HINT, text));
//    }

//    @Override
//    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY)
//    {
//        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
//    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderMain(matrices, mouseX, mouseY, delta);
        if (this.isSelected())
        {
//            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    protected void renderMain(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
//        int i = this.getYImage(this.isToggled());
        int i = 0;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        drawTexture(matrices, getX(), getY(), 0, 46 + i * 20, this.width / 2, this.height);
        drawTexture(matrices, getX() + this.width / 2, getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
//        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 0xFFFFFF : 0xA0A0A0;
        ClickableWidget.drawCenteredTextWithShadow(matrices, textRenderer, this.getMessage(), getX() + this.width / 2, getY() + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    public interface ToggleAction
    {
        void onToggle(ButtonWidget button, boolean toggled);
    }
}
