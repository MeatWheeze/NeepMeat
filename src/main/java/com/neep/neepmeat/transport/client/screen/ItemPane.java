package com.neep.neepmeat.transport.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.button.PersistentWidget;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.client.screen.util.BorderScrollRight;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.transport.network.SyncRequesterScreenS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ItemPane implements Drawable, Element, Selectable, GUIUtil
{
    protected final int wGrid, hGrid;
    protected final ItemRenderer itemRenderer;
    protected final TextRenderer textRenderer;

    protected final List<ResourceAmount<ItemVariant>> items;
    protected final MinecraftClient client;
    protected final int wSlot = 18;
    protected final int hSlot = 18;
    private final SearchWidget searchWidget;
    protected List<ItemStack> itemsToShow = List.of();
    protected int startX;
    protected int startY;
    protected int w;
    protected int h;
    protected int scrollRow;
    private BorderScrollRight border;
    private int scrollableRows;
    private boolean searchMode;

    public ItemPane(int wGrid, int hGrid, ItemRenderer itemRenderer, TextRenderer textRenderer, List<ResourceAmount<ItemVariant>> items, MinecraftClient client)
    {
        this.wGrid = wGrid;
        this.hGrid = hGrid;

        this.itemRenderer = itemRenderer;
        this.textRenderer = textRenderer;
        this.items = items;
        this.client = client;

        this.searchWidget = new SearchWidget();

        updateSearch();
    }

    public void updateSearch()
    {
        itemsToShow = items.stream()
                .map(i -> i.resource().toStack((int) i.amount()))
                .filter(s -> s.getName().getString().toLowerCase().contains(searchWidget.searchString))
                .toList();

        this.scrollableRows = (int) (Math.ceil((float) itemsToShow.size() / this.wGrid) - Math.min(hGrid, Math.ceil((float) itemsToShow.size()) / wGrid));
        scrollRow = MathHelper.clamp(scrollRow, 0, scrollableRows);
    }

    public void init(int startX, int startY)
    {
        this.startX = startX;
        this.startY = startY;

        this.w = wGrid * wSlot;
        this.h = hGrid * hSlot;

        this.border = new BorderScrollRight(startX - 2, startY - 3, this.w + 3, this.h + 4, 0, () -> PLCCols.BORDER.col)
        {
            @Override
            public void render(DrawContext context, int mouseX, int mouseY, float delta)
            {
                renderBorder(context, x, y, w - 1, h - 1, col.get(), 0, false);
                renderBorder(context, x, y, w - 1, h - 1, PLCCols.TRANSPARENT.col, -1, true);

                GUIUtil.drawVerticalLine1(context, x + w - 1, y + 1, y + h, PLCCols.TRANSPARENT.col);
            }

            void renderBorder(DrawContext context, int x, int y, int dx, int dy, int col, int offset, boolean bottom)
            {
                GUIUtil.drawHorizontalLine1(context, x - offset, x + dx + offset, y - offset, col);
                GUIUtil.drawVerticalLine1(context, x - offset, y - offset, y + dy + offset, col);
                if (bottom)
                    GUIUtil.drawHorizontalLine1(context, x - offset, x + dx + offset, y + dy + offset, col);

                GUIUtil.drawVerticalLine1(context, x + dx + offset, y - offset, y + 2, col);
                GUIUtil.drawVerticalLine1(context, x + dx + offset, y - offset + dy - 2, y + offset + dy, col);
            }
        };

        this.searchWidget.init(new Rectangle.Immutable(startX, startY + h, w, textRenderer.fontHeight));
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        border.render(matrices, mouseX, mouseY, delta, (float) scrollRow / scrollableRows);
        searchWidget.render(matrices, mouseX, mouseY, delta);

        int x, y, i, j;
        for (int m = 0; m < itemsToShow.size(); ++m)
        {
            matrices.getMatrices().push();
            i = m % wGrid;
            j = m / wGrid;

            if (j >= hGrid) break;

            x = startX + i * wSlot;
            y = startY + j * hSlot;

            drawSlot(x, y, matrices, getGridItem(i, j));

            matrices.getMatrices().pop();
        }

        ItemStack hoveredItem = getHoveredItem(mouseX, mouseY);
        if (hoveredItem != null)
        {
            matrices.drawItemTooltip(textRenderer, hoveredItem, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        scrollRow = (int) Math.min(scrollableRows, Math.max(scrollRow - amount, 0));
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
        ItemStack stack = getHoveredItem(mouseX, mouseY);
        if (stack != null && !stack.isEmpty())
        {
            long amount = Math.min(Screen.hasShiftDown() ? 1 : stack.getCount(), stack.getMaxCount());
            ResourceAmount<ItemVariant> requested = new ResourceAmount<>(ItemVariant.of(stack), amount);
            ClientPlayNetworking.send(SyncRequesterScreenS2CPacket.REQUEST_ID, SyncRequesterScreenS2CPacket.encodeRequest(PacketByteBufs.create(), requested));
            updateSearch();
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        if (searchMode)
        {
            searchWidget.write(chr, modifiers);
            return true;
        }

        if (chr == '/')
        {
            searchMode = true;
            return true;
        }

        return Element.super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && searchMode)
        {
            searchMode = false;
            searchWidget.searchString.setLength(0);
            updateSearch();
            return true;
        }
        else
        {
            if (searchMode)
            {
                if (keyCode == GLFW.GLFW_KEY_BACKSPACE)
                {
                    searchWidget.erase(-1);
                }
                return true;
            }
        }
        return false;
    }

    protected ItemStack getHoveredItem(double mouseX, double mouseY)
    {
        double x = mouseX - startX;
        double y = mouseY - startY;

        if (x < 0 || y < 0)
            return null;

        int i = (int) (x / wSlot);
        int j = (int) (y / hSlot);

        return getGridItem(i, j);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseInGrid(mouseX, mouseY);
    }

    @Override
    public boolean isFocused()
    {
        return true;
    }

    @Override
    public void setFocused(boolean focused)
    {

    }

    protected ItemStack getGridItem(int i, int j)
    {
        if (!isInGrid(i, j))
            return null;

        int m = scrollRow * wGrid + i + wGrid * j;

        if (!(m >= 0 && m < itemsToShow.size()))
            return null;

        return itemsToShow.get(m);
    }


    protected boolean mouseInGrid(double mouseX, double mouseY)
    {
        return mouseX - startX < w && mouseY - startY < h;
    }

    protected boolean isInGrid(int i, int j)
    {
        return i < wGrid && j < hGrid;
    }

    public void drawSlot(int x, int y, DrawContext matrices, ItemStack itemStack)
    {
        if (itemStack == null)
            return;

//        ItemStack itemStack = ra.resource().toStack((int) ra.amount());
        matrices.getMatrices().push();
        matrices.getMatrices().translate(x, y, 0);

//            this.setZOffset(100);
//            itemRenderer.zOffset = 100.0f;

//            RenderSystem.enableDepthTest();
//            this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemStack, x, y, slot.x + slot.y * this.backgroundWidth);
//            itemRenderer.renderItem(itemStack, ModelTransformationMode.GUI, 0xF000F0, OverlayTexture.DEFAULT_UV, matrices.getMatrices(), matrices.getVertexConsumers(), null, 0);
//            itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x, y, string);
        matrices.drawItem(itemStack, 0, 0, 100);
        matrices.drawItemInSlot(textRenderer, itemStack, 0, 0);
        matrices.getMatrices().pop();
    }

    public class SearchWidget extends PersistentWidget implements Drawable
    {
        private final Text searchMessage = NeepMeat.translationKey("screen", "guide.search");

        private final StringBuilder searchString = new StringBuilder();

        protected void write(char chr, int modifiers)
        {
            searchString.append(Character.toLowerCase(chr));
            updateSearch();
        }

        protected void erase(int dist)
        {
            if (searchString.length() == 0)
                return;
            searchString.delete(searchString.length() - 1, searchString.length());
            updateSearch();
        }

        private int x()
        {
            return bounds.x();
        }

        private int y()
        {
            return bounds.y();
        }

        private int w()
        {
            return bounds.w();
        }

        private int h()
        {
            return bounds.h();
        }

        @Override
        public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
        {
            GUIUtil.drawVerticalLine1(matrices, x() - 2, y() - 1, y() + h() + 3, PLCCols.BORDER.col);
            GUIUtil.drawVerticalLine1(matrices, x() - 1, y() - 1, y() + h() + 2, PLCCols.TRANSPARENT.col);

            GUIUtil.drawVerticalLine1(matrices, x() + w(), y() - 1, y() + h() + 3, PLCCols.BORDER.col);
            GUIUtil.drawVerticalLine1(matrices, x() + w() - 1, y() - 1, y() + h() + 2, PLCCols.TRANSPARENT.col);

            GUIUtil.drawHorizontalLine1(matrices, x(), x() + w(), y() + h() + 1, PLCCols.TRANSPARENT.col);
            GUIUtil.drawHorizontalLine1(matrices, x() - 1, x() + w(), y() + h() + 2, PLCCols.BORDER.col);

            if (!searchMode)
            {
                GUIUtil.drawText(matrices, textRenderer, searchMessage, x() + 2, y() + (h() - 7) / 2f, PLCCols.TEXT.col, false);
            }
            else
            {
                GUIUtil.drawText(matrices, textRenderer, "/" + searchString, x() + 2, y() + (h() - 7) / 2f, PLCCols.TEXT.col, false);
            }
        }
    }
}
