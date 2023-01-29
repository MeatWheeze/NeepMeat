package com.neep.neepmeat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.network.ScreenPropertyC2sPacket;
import com.neep.neepmeat.screen_handler.FluidRationerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class FluidRationerScreen extends HandledScreen<FluidRationerScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/fluid_rationer.png");
    protected TextField textField;

    protected static final TranslatableText BUCKET = new TranslatableText("screen." + NeepMeat.NAMESPACE + ".fluid_rationer.text.bucket");
    protected static final TranslatableText INGOT = new TranslatableText("screen." + NeepMeat.NAMESPACE + ".fluid_rationer.text.ingot");
    protected static final TranslatableText BOTTLE = new TranslatableText("screen." + NeepMeat.NAMESPACE + ".fluid_rationer.text.bottle");

    protected int slotX;
    protected int amountX;
    protected int amountY;
    protected Text amountText = new TranslatableText("screen." + NeepMeat.NAMESPACE + ".fluid_rationer.text.amount");

    public FluidRationerScreen(FluidRationerScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 178;
    }

    @Override
    protected void init()
    {
        super.init();

        this.slotX = x + 80;
        this.amountX = slotX + 18 + 3;
        this.amountY = y + 33;

        textField = new TextField(this.textRenderer, amountX, amountY, 3 * 18, 17, Text.of(""));
        textField.setText(Integer.toString(handler.getProperty(FluidRationerScreenHandler.PROP_TARGET_AMOUNT)));

        textField.setChangedListener(s ->
        {
            int parsed = !s.isEmpty() && s.matches("[0-9]*") ? Integer.parseInt(s) : 0;
//            this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket());
//            handler.setProperty(FluidRationerScreenHandler.PROP_TARGE_AMOUNT, parsed);
            ClientPlayNetworking.send(ScreenPropertyC2sPacket.ID, ScreenPropertyC2sPacket.create(FluidRationerScreenHandler.PROP_TARGET_AMOUNT, parsed));
        });
        this.addDrawableChild(textField);

        int startY = 20;
        int w = 40;
        int h = 20;
        this.addDrawableChild(new ButtonWidget(x + 5, y + startY, w, h, BOTTLE, button ->
        {
            textField.setText(String.valueOf(FluidConstants.BOTTLE));
        }));

        this.addDrawableChild(new ButtonWidget(x + 5, y + startY + (h + 1) , w, h, BUCKET, button ->
        {
            textField.setText(String.valueOf(FluidConstants.BUCKET));
        }));

        this.addDrawableChild(new ButtonWidget(x + 5, y + startY + 2 * (h + 1), w, h, INGOT, button ->
        {
            textField.setText(String.valueOf(FluidConstants.INGOT));
        }));

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
        this.textRenderer.draw(matrices, this.title, this.playerInventoryTitleX, this.titleY, 0x404040);
        this.textRenderer.draw(matrices, this.amountText, this.amountX, this.amountY - 10, 0x404040);
    }

    protected class TextField extends TextFieldWidget
    {
        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
        {
            super(textRenderer, x, y, width, height, text);
        }

        @Override
        public void write(String text)
        {
            if (!text.matches("[0-9]*")) return;
            super.write(text);
        }
    }
}