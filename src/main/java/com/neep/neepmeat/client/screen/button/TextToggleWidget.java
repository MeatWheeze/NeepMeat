package com.neep.neepmeat.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TextToggleWidget extends ClickableWidget implements GUIUtil
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
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
    {
        this.renderMain(context, mouseX, mouseY, delta);
        if (this.isHovered())
        {
            this.renderTooltip(context, mouseX, mouseY);
        }
    }

    protected void renderMain(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        int i = this.getYImage(toggled);
//        int i = 0; // TODO
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int borderCol = isHovered() || toggled ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
        int textCol = isHovered() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;

        matrices.drawTexture(NM_WIDGETS_TEXTURE, getX(), getY(), 0, 90, this.width / 2, this.height);
        matrices.drawTexture(NM_WIDGETS_TEXTURE, getX() + this.width / 2, getY(), 200 - this.width / 2, 90, this.width / 2, this.height);

        GUIUtil.renderBorder(matrices, getX() + 3, getY() + 3, width - 4 * 2 + 1, height - 4 * 2 + 1, borderCol, 0);

        GUIUtil.drawCenteredText(matrices, textRenderer, this.getMessage(), getX() + this.width / 2f, getY() + (this.height - 8) / 2f, textCol, false);
    }

    int getYImage(boolean toggled)
    {
        return toggled ? 2 : 1;
    }

    public interface ToggleAction
    {
        void onToggle(TextToggleWidget button, boolean toggled);
    }
}
