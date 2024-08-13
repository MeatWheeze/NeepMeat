package com.neep.neepmeat.neepbus.client.screen;

import com.neep.meatlib.client.screen.ParentWidget;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.neepmeat.client.screen.util.Background;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.neepbus.screen.AddressConfigHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class AddressConfigWidget extends ParentWidget
{
    private final AddressConfigHandler handler;

    public AddressConfigWidget(int x, int y, int w, int h, AddressConfigHandler handler)
    {
        super(x, y, w, h);
        this.handler = handler;
    }

    @Override
    public void init()
    {
        super.init();

        int headerHeight = 13;
        int entryHeight = 30;
        int columnWidth = 100; // Includes padding

        int numEntries = Math.max(handler.inputs.size(), handler.outputs.size());

        w = 2 * columnWidth;
        h = headerHeight + numEntries * entryHeight + 2;

        var background = addChild(new Background(x, y, w, h, 6));
        var bounds = background.withoutPadding();
        int entryWidth = bounds.w() / 2;

        for (int i = 0; i < handler.inputs.size(); ++i)
        {
            int finalI = i;
            addChild(new EntryWidget(textRenderer, bounds.x(), bounds.y() + headerHeight + 2 + entryHeight * i, entryWidth - 1, entryHeight - 1,
                    false, i, () -> handler.inputs.get(finalI)));
        }

        for (int i = 0; i < handler.outputs.size(); ++i)
        {
            int finalI = i;
            addChild(new EntryWidget(textRenderer, bounds.x() + bounds.w() / 2, bounds.y() + headerHeight + 2 + entryHeight * i, entryWidth - 1, entryHeight - 1,
                    true, i, () -> handler.outputs.get(finalI)));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

//        GUIUtil.drawScreenBorder(context, x, y, backgroundWidth, backgroundHeight);

        int headerHeight = 13;
        GUIUtil.drawHorizontalLine1(context, x, x + w, y + headerHeight, PLCCols.BORDER.col);
        GUIUtil.drawVerticalLine1(context, x + w / 2, y - 1, y + h, PLCCols.BORDER.col);
        GUIUtil.drawCenteredText(context, textRenderer, Text.of("Input"), x + w / 4f, y + 2, PLCCols.TEXT.col, false);
        GUIUtil.drawCenteredText(context, textRenderer, Text.of("Output"), x + 3 * w / 4f, y + 2, PLCCols.TEXT.col, false);
    }

    protected class EntryWidget extends NMTextField
    {
        private final Border border;
        private final Supplier<AddressConfigHandler.SyncEntry> entry;

        public EntryWidget(TextRenderer textRenderer, int x, int y, int width, int height, boolean isOutput, int idx, Supplier<AddressConfigHandler.SyncEntry> entry)
        {
            super(textRenderer, x, y, width, height, Text.of("ooer"));
            this.entry = entry;
            this.border = new Border(x, y, width, height, 0, () -> PLCCols.BORDER.col);
            drawFancyBackground(false);

            setText(entry.get().address());

            setChangedListener(newText ->
            {
                handler.addressChangeC2S.emitter().onAddressChange(isOutput, idx, newText);
            });
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
        {
            super.renderButton(context, mouseX, mouseY, delta);
            GUIUtil.drawText(context, textRenderer, Text.of(entry.get().name() + ": "), x() + 2, y(), PLCCols.TEXT.col, false);
        }

        @Override
        protected String getPrefix()
        {
            return "→";
        }

        @Override
        protected int getTextY()
        {
//            return super.getTextY();
            return y() + textRenderer.fontHeight;
        }

        @Override
        protected int getTextStart()
        {
            return w() - textRenderer.getWidth(getText()) -
                    (isFocused() ? 10 : 4); // Make room for _ cursor when selected
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            // Consume E keys to stop the inventory from being closed
            if (keyCode == GLFW.GLFW_KEY_E)
                return true;

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder)
        {

        }

        @Override
        public void playDownSound(SoundManager soundManager)
        {
//            soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}
