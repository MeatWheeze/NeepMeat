package com.neep.neepmeat.transport.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.network.SyncRequesterScreenS2CPacket;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(value= EnvType.CLIENT)
public class ItemRequesterScreen extends HandledScreen<ItemRequesterScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/item_requester.png");
    protected ItemPane itemPane;


    public ItemRequesterScreen(ItemRequesterScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 231;
    }

    @Override
    protected void init()
    {
        super.init();
        itemPane = new ItemPane(9, 7, x + 8, y + 8, itemRenderer, textRenderer, handler.getItems(), client);
        this.addDrawableChild(itemPane);

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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
//        ClientPlayNetworking.send(ScreenPropertyC2sPacket.ID, ScreenPropertyC2sPacket.create(ItemRequesterScreenHandler.PROP_SCROLL, (int) (amount * 1000)));
        Element e = hoveredElement(mouseX, mouseY).orElse(null);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
//        this.textRenderer.draw(matrices, this.title, this.playerInventoryTitleX, this.titleY, 0x404040);
    }

    public class ItemPane extends DrawableHelper implements Drawable, Element, Selectable
    {
        protected final int wGrid, hGrid;
        protected int wSlot = 18;
        protected int hSlot = 18;
        protected int startX;
        protected int startY;
        protected int width;
        protected int height;
        protected int scroll;
        protected int offset;
        protected final ItemRenderer itemRenderer;
        protected final TextRenderer textRenderer;
        protected final List<ResourceAmount<ItemVariant>> items;
        protected final MinecraftClient client;

        public ItemPane(int width, int height, int startX, int startY, ItemRenderer itemRenderer, TextRenderer textRenderer, List<ResourceAmount<ItemVariant>> items, MinecraftClient client)
        {
            this.wGrid = width;
            this.hGrid = height;

            this.width = wGrid * wSlot;
            this.height = hGrid * hSlot;

            this.startX = startX;
            this.startY = startY;
            this.itemRenderer = itemRenderer;
            this.textRenderer = textRenderer;
            this.items = items;
            this.client = client;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            int x, y, i, j;
            for (int m = 0; m < items.size(); ++m)
            {
                matrices.push();
                i = m % wGrid;
                j = m / wGrid;

                if (j >= hGrid) break;

                x = startX + i * wSlot;
                y = startY + j * hSlot;

                drawSlot(x, y, matrices, getGridItem(i, j));

                matrices.pop();
            }

            ResourceAmount<ItemVariant> ra = getHoveredItem(mouseX, mouseY);
            if (ra != null)
            {
                renderTooltip(matrices, ra.resource().toStack(), mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount)
        {
            scroll = (int) Math.max(scroll - amount, 0);
            int hiddenRows = (int) (Math.ceil((items.size() - (wGrid * hGrid)) / (float) wGrid));
            offset = (int) Math.min(hiddenRows, Math.max(offset - amount, 0));
            return Element.super.mouseScrolled(mouseX, mouseY, amount);
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
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            ResourceAmount<ItemVariant> ra = getHoveredItem(mouseX, mouseY);
            if (ra != null && !ra.resource().isBlank())
            {
                long amount = Math.min(Screen.hasShiftDown() ? 1 : ra.amount(), ra.resource().toStack().getMaxCount());
                ResourceAmount<ItemVariant> requested = new ResourceAmount<>(ra.resource(), amount);
                ClientPlayNetworking.send(SyncRequesterScreenS2CPacket.REQUEST_ID, SyncRequesterScreenS2CPacket.encodeRequest(PacketByteBufs.create(), requested));
            }
            return false;
        }

        protected ResourceAmount<ItemVariant> getHoveredItem(double mouseX, double mouseY)
        {
            double x = mouseX - startX;
            double y = mouseY - startY;

            if (x < 0 || y < 0) return null;

            int i = (int) (x / wSlot);
            int j = (int) (y / hSlot);

            return getGridItem(i, j);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY)
        {
            return mouseInGrid(mouseX, mouseY);
        }

        protected ResourceAmount<ItemVariant> getGridItem(int i, int j)
        {
            if (!isInGrid(i, j)) return null;

            int m = offset * wGrid + i + wGrid * j;

            if (!(m >= 0 && m < items.size())) return null;

            return items.get(m);
        }


        protected boolean mouseInGrid(double mouseX, double mouseY)
        {
            return mouseX - startX < width && mouseY - startY < height;
        }
        protected boolean isInGrid(int i, int j)
        {
            return i < wGrid && j < hGrid;
        }

        public void drawSlot(int x, int y, MatrixStack matrices, ResourceAmount<ItemVariant> ra)
        {
            if (ra == null) return;

            ItemStack itemStack = ra.resource().toStack((int) ra.amount());
            String string = null;

            this.setZOffset(100);
            itemRenderer.zOffset = 100.0f;

            RenderSystem.enableDepthTest();
//            this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemStack, x, y, slot.x + slot.y * this.backgroundWidth);
            itemRenderer.renderInGuiWithOverrides(itemStack, x, y);
            itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x, y, string);

            itemRenderer.zOffset = 0.0f;
            this.setZOffset(0);
        }
    }
}