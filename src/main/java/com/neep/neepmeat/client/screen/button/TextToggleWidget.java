package com.neep.neepmeat.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TextToggleWidget extends ButtonWidget
{
    public static final ButtonWidget.TooltipSupplier EMPTY = (button, matrices, mouseX, mouseY) -> {};

    protected final ButtonWidget.TooltipSupplier tooltipSupplier;
    protected boolean toggled;
    protected ToggleAction onToggle;

    public TextToggleWidget(int x, int y, int width, int height, Text message, boolean toggled, ToggleAction onToggle)
    {
        this(x, y, width, height, message, toggled, onToggle, EMPTY);
    }

    public TextToggleWidget(int x, int y, int width, int height, Text message, boolean toggled, ToggleAction onToggle, ButtonWidget.TooltipSupplier tooltipSupplier)
    {
        super(x, y, width, height, message, b -> {});
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

    @Override
    public void appendNarrations(NarrationMessageBuilder builder)
    {
        this.appendDefaultNarrations(builder);
        this.tooltipSupplier.supply(text -> builder.put(NarrationPart.HINT, text));
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY)
    {
        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderMain(matrices, mouseX, mouseY, delta);
        if (this.isHovered())
        {
            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    protected void renderMain(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        int i = this.getYImage(this.isToggled());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 0xFFFFFF : 0xA0A0A0;
        ClickableWidget.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    public interface ToggleAction
    {
        void onToggle(ButtonWidget button, boolean toggled);
    }
}
