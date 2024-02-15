package com.neep.neepmeat.machine.separator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.NumberFieldWidget;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SeparatorScreen extends HandledScreen<SeparatorScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/separator.png");
    private static final Text TOOLTIP = Text.translatable("screen." + NeepMeat.NAMESPACE + ".separator.text.remainder");

    public SeparatorScreen(SeparatorScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.backgroundWidth = 83;
        this.backgroundHeight = 55;
    }

    @Override
    protected void init()
    {
        super.init();

        NumberFieldWidget textField = new NumberFieldWidget(this.textRenderer, x + 6, y + 7, 50, 17, Text.of(""))
        {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
            {
                super.renderButton(matrices, mouseX, mouseY, delta);
                if (isMouseOver(mouseX, mouseY))
                {
                    SeparatorScreen.this.renderTooltip(matrices, TOOLTIP, mouseX, mouseY);
                }
            }
        };
        textField.setText(Integer.toString(handler.getProperty(SeparatorBlockEntity.Properties.REMAINDER.ordinal())));

        textField.setChangedListener(s ->
        {
            int parsed = !s.isEmpty() && s.matches("[0-9]*") ? Integer.parseInt(s) : 0;
            handler.setRemainder(parsed);
        });
        textField.setDrawsBackground(false);

        this.addDrawableChild(new ButtonWidget(x + 5, y + 30, 73, 20, Text.empty(), button ->
                handler.setTakeBabies(!handler.takeBabies()),
                (button, matrices, mouseX, mouseY) ->
                {
                })
        {
            @Override
            public Text getMessage()
            {
                return handler.takeBabies() ?
                        Text.of("Take babies") : Text.of("Take adults");
            }
        });

        this.addDrawableChild(textField);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        super.renderBackground(matrices);
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
//        super.drawForeground(matrices, mouseX, mouseY);
    }
}
