package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.api.implant.ImplantAttributes;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.implant.player.ImplantManager;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.screen_handler.UpgradeManagerScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class UpgradeManagerScreen extends HandledScreen<UpgradeManagerScreenHandler>
{
    @Nullable private Identifier selected;
    @Nullable private ImplantManager implantManager;
    @Nullable private MutateInPlace<?> mip;
    private int entryWidth = 120;

    public UpgradeManagerScreen(UpgradeManagerScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 450;
        this.backgroundHeight = 200;

        super.init();

        int listX = x + 2;
        int listY = y + 2;
        int entryHeight = 18;

        ImplantManager manager = handler.getImplantManager();
        if (manager != null)
        {
            this.implantManager = manager;
            for (var entry : handler.getImplantManager().getInstalled())
            {
                addDrawableChild(new InstalledWidget(entry, listX, listY, entryWidth, entryHeight));
                listY += entryHeight + 2;
            }
        }

        int removeButtonWidth = 100;
        addDrawableChild(new RemoveButton(x + backgroundWidth - (removeButtonWidth + 2), y + 2, removeButtonWidth, 10,
                Text.translatable("screen.neepmeat.upgrade_manager.remove")));
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        super.renderBackground(matrices);
        GUIUtil.renderBorder(matrices, x, y, backgroundWidth, backgroundHeight, PLCCols.BORDER.col, 0);
        GUIUtil.drawVerticalLine1(matrices, x + entryWidth + 4, y, y + backgroundHeight, PLCCols.BORDER.col);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
        if (mip == null)
        {
            Text message1 = Text.of("No valid PLC workbench found.");
            Text message2 = Text.of("Place a PLC workbench in front of the machine.");
            int textWidth = textRenderer.getWidth(message1);
            textRenderer.drawWithShadow(matrices, message1, (backgroundWidth - textWidth) / 2f, 20, PLCCols.TEXT.col);
            textWidth = textRenderer.getWidth(message2);
            textRenderer.drawWithShadow(matrices, message2, (backgroundWidth - textWidth) / 2f, 20 + textRenderer.fontHeight * 1.5f, PLCCols.TEXT.col);
        }
        else if (implantManager == null)
        {
            Text message1 = Text.of("No upgradable object found.");
            Text message2 = Text.of("Insert an item or entity into the connected workbench.");
            int textWidth = textRenderer.getWidth(message1);
            textRenderer.drawWithShadow(matrices, message1, (backgroundWidth - textWidth) / 2f, 20, PLCCols.TEXT.col);
            textWidth = textRenderer.getWidth(message2);
            textRenderer.drawWithShadow(matrices, message2, (backgroundWidth - textWidth) / 2f, 20 + textRenderer.fontHeight * 1.5f, PLCCols.TEXT.col);
        }
    }

    @Override
    protected void handledScreenTick()
    {
        if (client.world.getTime() % 4 == 0)
        {
            this.mip = handler.getMip();

            ImplantManager newManager = null;
            if (this.mip != null)
            {
                Object object = mip.get();
                if (object != null)
                {
                    newManager = NMComponents.IMPLANT_MANAGER.getNullable(object);
                }
            }

            if (newManager != this.implantManager)
            {
                this.implantManager = newManager;
                clearAndInit();
            }
        }
    }

    private void onRemoveButton()
    {
        ImplantManager manager = handler.getImplantManager();
        if (manager != null && selected != null)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIdentifier(selected);
            ClientPlayNetworking.send(UpgradeManagerScreenHandler.HANDLER_ID, buf);

            manager.removeImplant(selected);
            clearAndInit();
        }
        else
        {
            client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.ERROR, 1));
        }
    }

    private void select(Identifier id)
    {
        this.selected = id;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
    }

    class RemoveButton extends ClickableWidget
    {
        public RemoveButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {
            builder.put(NarrationPart.TITLE, Text.literal("remove"));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            int col = PLCCols.INVALID.col;
            if (selected != null)
            {
                col = isMouseOver(mouseX, mouseY) ? PLCCols.SELECTED.col : PLCCols.TEXT.col;
            }

            GUIUtil.renderBorder(matrices, x, y, width, height,  col, 0);
            ClickableWidget.drawCenteredText(matrices, textRenderer, this.getMessage(),
                    x + width / 2, y + (height - 8) / 2, PLCCols.TEXT.col);
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            onRemoveButton();
        }

        @Override
        public void playDownSound(SoundManager soundManager)
        {
            soundManager.play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0f));
        }
    }

    class InstalledWidget extends ClickableWidget
    {
        private final Identifier id;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public InstalledWidget(Identifier id, int x, int y, int width, int height)
        {
            super(x, y, width, height, Text.empty());
            this.id = id;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            int textCol = id.equals(selected) ? PLCCols.SELECTED.col : PLCCols.TEXT.col;
            GUIUtil.renderBorder(matrices, x, y, width, height, textCol, 0);

            Text name = ImplantAttributes.getName(id);
            textRenderer.draw(matrices, name, x + 2, y + 2, textCol);
        }

        @Override
        public SelectionType getType()
        {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            select(id);
        }

        @Override
        public void playDownSound(SoundManager soundManager)
        {
            soundManager.play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0f));
        }
    }
}
