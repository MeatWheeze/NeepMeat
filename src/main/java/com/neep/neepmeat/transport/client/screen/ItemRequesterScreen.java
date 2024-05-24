package com.neep.neepmeat.transport.client.screen;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

@Environment(value = EnvType.CLIENT)
public class ItemRequesterScreen extends HandledScreen<ItemRequesterScreenHandler>
{
    private final ItemPane itemPane;

    public ItemRequesterScreen(ItemRequesterScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.backgroundWidth = 11 * 18 + 14;
        this.backgroundHeight = 231;

        MinecraftClient client1 = MinecraftClient.getInstance();
        this.itemPane = new ItemPane(11, 7, client1.getItemRenderer(), client1.textRenderer, handler.getItems(), MinecraftClient.getInstance());
    }

    @Override
    protected void init()
    {
        super.init();

        itemPane.init(x + 8, y + 8);
        this.addDrawableChild(itemPane);

        Border border = addDrawable(new Border(x, (height - backgroundHeight) / 2, backgroundWidth, backgroundHeight, 3, () -> PLCCols.BORDER.col));
        Rectangle bounds = border.withoutPadding();

//        Rectangle inv = new Rectangle.Immutable(bounds.x() + 3, bounds.y() + bounds.h() - BasicScreenHandler.playerSlotsH() - 3, BasicScreenHandler.playerSlotsW(), BasicScreenHandler.playerSlotsH());
        var invBorder = addDrawable(new PlayerSlotsBorder(bounds.x() + 3, bounds.y() + bounds.h() - BasicScreenHandler.playerInvH() - 3, () -> PLCCols.BORDER.col));

//        addDrawable(new Border())

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
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType)
    {
        super.onMouseClick(slot, slotId, button, actionType);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (itemPane.keyPressed(keyCode, scanCode, modifiers))
            return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        return itemPane.charTyped(chr, modifiers);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
//        this.textRenderer.draw(matrices, this.title, this.playerInventoryTitleX, this.titleY, 0x404040);
    }

    public void updateItems()
    {
        itemPane.updateSearch();
    }
}