package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NMButtonWidget extends ButtonWidget
{
    public static final Identifier WIDGETS_TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/inventory_background.png");

    public NMButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier)
    {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    @Override
    protected void renderButton(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        matrices.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        matrices.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        matrices.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int borderCol = isHovered() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
        GUIUtil.renderBorder(matrices, getX() + 3, getY() + 3, width - 4 * 2 + 1, height - 4 * 2 + 1, borderCol, 0);

        int textCol = isHovered() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
        this.drawMessage(matrices, minecraftClient.textRenderer, textCol);
    }

    private int getTextureY()
    {
        return 90; // V offset for button background
//        int i = 1;
//        if (!this.active)
//        {
//            i = 0;
//        }
//        else if (this.isSelected())
//        {
//            i = 2;
//        }
//
//        return 46 + i * 20;
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color)
    {
        this.drawScrollableText(context, textRenderer, 4, color);
    }
}
