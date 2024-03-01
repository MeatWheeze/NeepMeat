package com.neep.neepmeat.transport.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import com.neep.neepmeat.transport.screen_handler.LimiterValveScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

import static com.neep.neepmeat.transport.screen_handler.LimiterValveScreenHandler.PROP_MB_MODE;

@Environment(value= EnvType.CLIENT)
public class LimiterValveScreen extends HandledScreen<LimiterValveScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/limiter_valve.png");
    protected static final Text RATE = Text.translatable("screen." + NeepMeat.NAMESPACE + ".limiter_valve.text.rate");
    protected static final Text UNIT = Text.translatable("screen." + NeepMeat.NAMESPACE + ".limiter_valve.text.unit");
    protected static final Text DROPLET_MODE = Text.translatable("screen." + NeepMeat.NAMESPACE + ".limiter_valve.text.droplet");
    protected static final MutableText DROPLET_MODE_INFO = Text.translatable("screen." + NeepMeat.NAMESPACE + ".limiter_valve.text.droplet_info").formatted(Formatting.GRAY);
    protected static final Text MB_MODE = Text.translatable("screen." + NeepMeat.NAMESPACE + ".limiter_valve.text.mb");
    protected static final MutableText MB_MODE_INFO = Text.translatable("screen." + NeepMeat.NAMESPACE + ".limiter_valve.text.mb_info").formatted(Formatting.GRAY);

    protected TextField textField;

    public LimiterValveScreen(LimiterValveScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 87;
    }

    @Override
    protected void init()
    {
        super.init();

        this.titleX = 29;
        int textFieldWidth = 3 * 18;
        int buttonWidth = 60;
        int spacer = 6;

        int buttonHeight = 20;

        int buttonX = x + (backgroundWidth - (textFieldWidth + spacer + buttonWidth)) / 2;
        int buttonY = y + (backgroundHeight - buttonHeight) / 2;
        int textFieldX = buttonX + buttonWidth + spacer;

        textField = new TextField(this.textRenderer, textFieldX, buttonY, textFieldWidth, buttonHeight, Text.of(""))
        {
            @Override
            public void render(DrawContext context, int mouseX, int mouseY, float delta)
            {
                super.render(context, mouseX, mouseY, delta);
                if (isMouseOver(mouseX, mouseY)) // Porting jank
                {
                    context.drawTooltip(textRenderer, RATE, mouseX, mouseY);
                }
            }
            //            @Override
//            public void renderTooltip(DrawContext matrices, int mouseX, int mouseY)
//            {
//                LimiterValveScreen.this.renderTooltip(matrices, RATE, mouseX, mouseY);
//                super.renderTooltip(matrices, mouseX, mouseY);
//            }
        };
        textField.setText(Integer.toString(handler.getProperty(LimiterValveScreenHandler.PROP_MAX_AMOUNT)));

        textField.setChangedListener(s ->
        {
            int parsed = !s.isEmpty() && s.matches("[0-9]*") ? Integer.parseInt(s) : 0;
            ClientPlayNetworking.send(ScreenPropertyC2SPacket.ID, ScreenPropertyC2SPacket.Client.create(LimiterValveScreenHandler.PROP_MAX_AMOUNT, parsed));
        });
        this.addDrawableChild(textField);

        this.addDrawableChild(new ButtonWidget(buttonX, buttonY, buttonWidth, buttonHeight, getButtonText(), button -> {}, textSupplier -> getButtonText().copy())
        {
            @Override
            public void onPress()
            {
                super.onPress();
                int oldMode = handler.getProperty(PROP_MB_MODE);
                int newMode = oldMode == 0 ? 1 : 0;
                handler.setProperty(PROP_MB_MODE, newMode);
                updateText(oldMode, newMode);
                setMessage(getButtonText());

                // Update server
                ClientPlayNetworking.send(ScreenPropertyC2SPacket.ID, ScreenPropertyC2SPacket.Client.create(PROP_MB_MODE, newMode));
            }

            @Override
            public void render(DrawContext context, int mouseX, int mouseY, float delta)
            {
                super.render(context, mouseX, mouseY, delta);
                if (isMouseOver(mouseX, mouseY)) // Porting jank
                {
                    context.drawTooltip(textRenderer, getButtonTooltip(), mouseX, mouseY);
                }
            }

            //            @Override
//            public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY)
//            {
//                LimiterValveScreen.this.renderTooltip(matrices, getButtonTooltip(), mouseX, mouseY);
//                super.renderTooltip(matrices, mouseX, mouseY);
//            }
        });
    }

    protected Text getButtonText()
    {
        return handler.getProperty(PROP_MB_MODE) == 0 ? DROPLET_MODE : MB_MODE;
    }

    protected List<Text> getButtonTooltip()
    {
        return List.of(
                UNIT,
                handler.getProperty(PROP_MB_MODE) == 0 ? DROPLET_MODE_INFO : MB_MODE_INFO
        );
    }

    protected void updateText(int oldMode, int newMode)
    {
        if (oldMode != newMode)
        {
            String oldText = textField.getText();
            int parsed = !oldText.isEmpty() && oldText.matches("[0-9]*") ? Integer.parseInt(oldText) : 0;

            boolean toMb = newMode == 1;

            textField.setText(String.valueOf(toMb ? parsed / 81 : parsed * 81));
        }
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY)
    {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        matrices.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType)
    {
        super.onMouseClick(slot, slotId, button, actionType);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    protected void drawForeground(DrawContext matrices, int mouseX, int mouseY)
    {
        GUIUtil.drawText(matrices, textRenderer, this.title, this.playerInventoryTitleX, this.titleY, 0x404040,false);
    }

    protected static class TextField extends TextFieldWidget
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