package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.neepmeat.client.screen.util.Background;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.neepbus.screen.NeepBusScreenHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class NeepBusConfigScreen extends HandledScreen<NeepBusScreenHandler>
{
    public NeepBusConfigScreen(NeepBusScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
    }

    @Override
    protected void init()
    {
        int headerHeight = 13;
        int entryHeight = 30;

        int columnWidth = 100; // Includes padding

        backgroundWidth = 2 * columnWidth;

        int numEntries = 2;

        backgroundHeight = headerHeight + numEntries * entryHeight + 2;

        super.init();

        var background = addDrawable(new Background(x, y, backgroundWidth, backgroundHeight, 6, () -> PLCCols.BORDER.col));
        var bounds = background.withoutPadding();
        int entryWidth = bounds.w() / 2;


        for (int i = 0; i < handler.inputs.size(); ++i)
        {
            int finalI = i;
            addDrawableChild(new EntryWidget(textRenderer, bounds.x(), bounds.y() + headerHeight + 2 + entryHeight * i, entryWidth - 1, entryHeight - 1,
                    false, i, () -> handler.inputs.get(finalI)));
        }

        for (int i = 0; i < handler.outputs.size(); ++i)
        {
            int finalI = i;
            addDrawableChild(new EntryWidget(textRenderer, bounds.x() + bounds.w() / 2, bounds.y() + headerHeight + 2 + entryHeight * i, entryWidth - 1, entryHeight - 1,
                    true, i, () -> handler.outputs.get(finalI)));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

//        GUIUtil.drawScreenBorder(context, x, y, backgroundWidth, backgroundHeight);

        int headerHeight = 13;
        GUIUtil.drawHorizontalLine1(context, x, x + backgroundWidth, y + headerHeight, PLCCols.BORDER.col);
        GUIUtil.drawVerticalLine1(context, x + backgroundWidth / 2, y - 1, y + backgroundHeight, PLCCols.BORDER.col);
        GUIUtil.drawCenteredText(context, textRenderer, Text.of("Input"), x + backgroundWidth / 4f, y + 2, PLCCols.TEXT.col, false);
        GUIUtil.drawCenteredText(context, textRenderer, Text.of("Output"), x + 3 * backgroundWidth / 4f, y + 2, PLCCols.TEXT.col, false);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }

//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
//    {
//        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc())
//        {
//            this.close();
//            return true;
//        }
//
//        for (var entry : entries)
//        {
//            if (entry.keyPressed(keyCode, scanCode, modifiers))
//                return true;
//        }
//
//        return false;
//    }

    private class EntryWidget extends NMTextField
    {
        private final Border border;
        private final Supplier<NeepBusScreenHandler.SyncEntry> entry;

        public EntryWidget(TextRenderer textRenderer, int x, int y, int width, int height, boolean isOutput, int idx, Supplier<NeepBusScreenHandler.SyncEntry> entry)
        {
            super(textRenderer, x, y, width, height, Text.of("ooer"));
            this.entry = entry;
            this.border = new Border(x, y, width, height, 0, () -> PLCCols.BORDER.col);
            drawFancyBackground(false);

            setText(entry.get().address());

            setChangedListener(newText ->
            {
                getScreenHandler().addressChangeC2S.emitter().onAddressChange(isOutput, idx, newText);
            });
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
        {
            super.renderButton(context, mouseX, mouseY, delta);
        }

        @Override
        protected int getTextY()
        {
//            return super.getTextY();
            return y() + 1;
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
