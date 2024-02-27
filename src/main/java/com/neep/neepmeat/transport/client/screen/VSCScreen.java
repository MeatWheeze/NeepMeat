package com.neep.neepmeat.transport.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import com.neep.neepmeat.transport.block.energy_transport.entity.VSCBlockEntity;
import com.neep.neepmeat.transport.screen_handler.VSCScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VSCScreen extends HandledScreen<VSCScreenHandler>
{
    private final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/vsc.png");
    private static final Text TOOLTIP = Text.translatable("screen." + NeepMeat.NAMESPACE + ".vsc.text.power");

    private TextField textField;

    public VSCScreen(VSCScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 64;
        this.backgroundHeight = 32;

        super.init();

        textField = new TextField(this.textRenderer, x + 6, y + 7, 3 * 18, 17, Text.of(""))
        {
            @Override
            public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
            {
                super.renderButton(context, mouseX, mouseY, delta);
                if (isMouseOver(mouseX, mouseY))
                {
//                    VSCScreen.this.renderTooltip(matrices, TOOLTIP, mouseX, mouseY);
                }
            }
        };
        textField.setText(Integer.toString(handler.getProperty(VSCBlockEntity.VSCDelegate.Names.POWER_FLOW_EJ.ordinal())));
        textField.setDrawsBackground(false);

        textField.setChangedListener(s ->
        {
            int parsed = !s.isEmpty() && s.matches("[0-9]*") ? Integer.parseInt(s) : 0;
            ClientPlayNetworking.send(ScreenPropertyC2SPacket.ID, ScreenPropertyC2SPacket.Client.create(VSCBlockEntity.VSCDelegate.Names.POWER_FLOW_EJ.ordinal(), parsed));
        });
        this.addDrawableChild(textField);
    }

    @Override
    protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY)
    {
        super.renderBackground(matrices);
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        matrices.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext matrices, int mouseX, int mouseY)
    {
    }

    protected static class TextField extends TextFieldWidget
    {
        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
        {
            super(textRenderer, x, y, width, height, text);
        }

//        @Override
//        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
//        {
//            int i;
//            if (!this.isVisible())
//            {
//                return;
//            }
//
//            i = this.isFocused() ? -1 : -6250336;
//            TextFieldWidget.fill(matrices, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
//            TextFieldWidget.fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
//
//            i = this.editable ? this.editableColor : this.uneditableColor;
//            int j = this.selectionStart - this.firstCharacterIndex;
//            int k = this.selectionEnd - this.firstCharacterIndex;
//            String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
//            boolean bl = j >= 0 && j <= string.length();
//            boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && bl;
//            int l = this.drawsBackground ? this.x + 4 : this.x;
//            int m = this.drawsBackground ? this.y + (this.height - 8) / 2 : this.y;
//            int n = l;
//            if (k > string.length()) {
//                k = string.length();
//            }
//            if (!string.isEmpty()) {
//                String string2 = bl ? string.substring(0, j) : string;
//                n = this.textRenderer.drawWithShadow(matrices, this.renderTextProvider.apply(string2, this.firstCharacterIndex), (float)n, (float)m, i);
//            }
//            boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
//            int o = n;
//            if (!bl) {
//                o = j > 0 ? l + this.width : l;
//            } else if (bl3) {
//                --o;
//                --n;
//            }
//            if (!string.isEmpty() && bl && j < string.length()) {
//                this.textRenderer.drawWithShadow(matrices, this.renderTextProvider.apply(string.substring(j), this.selectionStart), (float)n, (float)m, i);
//            }
//            if (!bl3 && this.suggestion != null) {
//                this.textRenderer.drawWithShadow(matrices, this.suggestion, (float)(o - 1), (float)m, -8355712);
//            }
//            if (bl2) {
//                if (bl3) {
//                    DrawableHelper.fill(matrices, o, m - 1, o + 1, m + 1 + this.textRenderer.fontHeight, -3092272);
//                } else {
//                    this.textRenderer.drawWithShadow(matrices, HORIZONTAL_CURSOR, (float)o, (float)m, i);
//                }
//            }
//            if (k != j) {
//                int p = l + this.textRenderer.getWidth(string.substring(0, k));
//                this.drawSelectionHighlight(o, m - 1, p - 1, m + 1 + this.textRenderer.fontHeight);
//            }
//        }

        @Override
        public void write(String text)
        {
            if (!text.matches("[0-9]*"))
                return;
            super.write(text);
        }
    }
}
