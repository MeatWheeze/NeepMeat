package com.neep.meatweapons.client.screen.meatgun;

import com.neep.meatlib.client.ClientChannelSender;
import com.neep.meatlib.network.Sender;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.meatgun.MeatgunModuleItem;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.meatgun.module.ModuleSlot;
import com.neep.meatweapons.screen.TinkerTableScreenHandler;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class TreePane extends TinkerTableScreen.PaneWidget
{
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final List<ModuleWidget> moduleWidgets = new ArrayList<>();
    private final Slot slot;
    @Nullable private MeatgunComponent meatgun;
    private boolean scissor;

    private float ox, oy;

    private final Sender<TinkerTableScreenHandler.SlotClick> sender;

    public TreePane(TinkerTableScreenHandler handler, Slot itemSlot)
    {
        this.slot = itemSlot;
        this.sender = new ClientChannelSender<>(TinkerTableScreenHandler.CHANNEL_ID, TinkerTableScreenHandler.CHANNEL_FORMAT);
    }

    @Override
    public void init(Rectangle parentSize)
    {
        super.init(parentSize);
        ox = bounds.x() + (float) bounds.w() / 2;
        oy = bounds.y() + 10;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        enableScissor(context);
        for (var moduleWidget : moduleWidgets)
        {
            moduleWidget.render(context, mouseX, mouseY, delta);
        }
        disableScissor(context);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
//        if (button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_3)
        {
            double thing = 1;
            ox += deltaX * thing;
            oy += deltaY * thing;

            updateWidgetOrigins();
            return true;
        }
//        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateWidgetOrigins()
    {
        for (var widget : moduleWidgets)
        {
            widget.updateOrigin(Math.round(ox), Math.round(oy));
        }
    }

    private void reorganise()
    {
        moduleWidgets.clear();
        if (meatgun == null)
            return;

        List<List<ModuleWidget>> levels = new ArrayList<>();
        MeatgunModule root = meatgun.getRoot();
        process(levels, 0, root);

        int thingHeight = ModuleWidget.height() + 5;
        int offsetY = 0;
        for (var level : levels)
        {

            int totalWidth = 0;
            int padding = 4;
            for (var widget : level)
            {
                totalWidth += widget.width() + padding;
            }

            int offsetX = -totalWidth / 2;
            for (var widget : level)
            {
                widget.init(offsetX, offsetY);
                offsetX += widget.width() + padding;

                moduleWidgets.add(widget);
            }

            offsetY += thingHeight;
        }
        updateWidgetOrigins();
    }

    private void process(List<List<ModuleWidget>> levels, int level, MeatgunModule module)
    {
        if (levels.size() == level)
        {
            levels.add(new ArrayList<>());
        }

        ModuleWidget widget = new ModuleWidget(textRenderer, module);
        levels.get(level).add(widget);

        for (var slot : module.getChildren())
        {
            if (slot.get() == MeatgunModule.DEFAULT)
                continue;

            process(levels, level + 1, slot.get());
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (bounds.isWithin(mouseX, mouseY))
        {
            for (var widget : moduleWidgets)
            {
                if (widget.mouseClicked(mouseX, mouseY, button))
                    return true;
            }
        }
        return false;
    }

    private void enableScissor(DrawContext context)
    {
        context.enableScissor(bounds.x() + 2, bounds.y() + 2, bounds.x() + bounds.w() - 2, bounds.y() + bounds.h() - 2);
        scissor = true;
    }

    private void disableScissor(DrawContext context)
    {
        context.disableScissor();
        scissor = false;
    }

    public void tick()
    {
        meatgun = MWComponents.MEATGUN.getNullable(slot.getStack());
//        if (foundComponent != meatgun)
//        {
//            foundComponent = meatgun;
//        }
        reorganise();
    }

    private class ModuleSlotWidget implements Drawable
    {
        private static final Rectangle size = new Rectangle.Immutable(0, 0, 18, 18);
        private final TextRenderer textRenderer;
        private final MeatgunModule module;
        private final int slotIdx;
        private final ModuleSlot slot1;
        private Rectangle local = new Rectangle.Immutable(size);
        private Rectangle bounds = new Rectangle.Immutable(size);

        private final ItemStack defaultStack;

        public ModuleSlotWidget(TextRenderer textRenderer, MeatgunModule module, int slotIdx, ModuleSlot slot)
        {
            this.textRenderer = textRenderer;
            this.module = module;
            this.slotIdx = slotIdx;
            slot1 = slot;
            MeatgunModule.Type<?> type = slot1.get().getType();
            if (type != MeatgunModule.DEFAULT_TYPE)
                defaultStack = MeatgunModuleItem.get(type);
            else
                defaultStack = ItemStack.EMPTY;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            int bordereCol = PLCCols.BORDER.col;
            if (bounds.isWithin(mouseX, mouseY))
            {
                bordereCol = PLCCols.SELECTED.col;
                renderTooltip(context, mouseX, mouseY, delta);
            }
            GUIUtil.drawTexture(TinkerTableScreen.WIDGETS_TEXTURE, context, bounds.x(), bounds.y(), 0, 0, 18, 19, bordereCol);
            context.drawItem(defaultStack, bounds.x() + 1, bounds.y() + 1);
        }

        private void renderTooltip(DrawContext context, int mouseX, int mouseY, float delta)
        {
            if (defaultStack.isEmpty())
                return;

            // There must be an easier way
            if (scissor)
            {
                disableScissor(context);
                context.drawItemTooltip(textRenderer, defaultStack, mouseX, mouseY);
                context.draw();
                enableScissor(context);
            }
            else
                context.drawItemTooltip(textRenderer, defaultStack, mouseX, mouseY);
        }

        public void updateOrigin(int ox, int oy)
        {
            bounds = local.offset(ox, oy);
        }

        public static int width()
        {
            return size.w();
        }

        public static int height()
        {
            return size.h();
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (bounds.isWithin(mouseX, mouseY))
            {
                sender.emitter().apply(module.getUuid(), slotIdx);

                return true;
            }
            return false;
        }

        public void init(int x, int y)
        {
            local = size.offset(x, y);
        }
    }

    private class ModuleWidget implements Drawable
    {
        private final MeatgunModule module;
        private int ox, oy;
        private int absX, absY;

        private final Rectangle.Mutable bounds = new Rectangle.Mutable(0, 0, 0, 0);
        private final TextRenderer textRenderer;

        private final List<ModuleSlotWidget> slots = new ArrayList<>();

        public ModuleWidget(TextRenderer textRenderer, MeatgunModule module)
        {
            this.textRenderer = textRenderer;
            this.module = module;

            for (int slotIdx = 0; slotIdx < module.getChildren().size(); slotIdx++)
            {
                var slot = module.getChildren().get(slotIdx);
                ModuleSlotWidget slotWidget = new ModuleSlotWidget(textRenderer, module, slotIdx, slot);
                slots.add(slotWidget);
            }
        }

        public void init(int x, int y)
        {
            this.absX = x;
            this.absY = y;
            updateBounds();

            if (slots.size() > 0)
            {
                float spacing = (float) bounds.w() / slots.size();
                int offsetX = (int) (width() - (18 * slots.size() + (slots.size() - 1) * (spacing - 18))) / 2;
                for (var slot : slots)
                {
                    slot.init(bounds.x() + offsetX, bounds.y() + bounds.h() - ModuleSlotWidget.height() - 2);
                    offsetX += spacing;
                }
            }
        }

        public void updateOrigin(int ox, int oy)
        {
            this.ox = ox;
            this.oy = oy;

            for (var slot : slots)
            {
                slot.updateOrigin(ox, oy);
            }

            updateBounds();
        }

        private void updateBounds()
        {
            this.bounds.set(ox + absX, oy + absY, width(), height());
        }

        public int width()
        {
            return textRenderer.getWidth(getName()) + 4;
        }

        public static int height()
        {
            return ModuleSlotWidget.height() + 9 + 5;
        }

        public Text getName()
        {
            return Text.translatable(module.getType().getId().toTranslationKey("meatgun_module"));
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            GUIUtil.drawText(context, textRenderer, getName(), bounds.x() + 3, bounds.y() + 2, PLCCols.TEXT.col, false);
            GUIUtil.renderBorder(context, bounds.x(), bounds.y(), bounds.w(), bounds.h(), PLCCols.BORDER.col, 0);

            for (var slot : slots)
            {
                slot.render(context, mouseX, mouseY, delta);
            }
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (bounds.isWithin(mouseX, mouseY))
            {
                for (var slot : slots)
                {
                    if (slot.mouseClicked(mouseX, mouseY, button))
                        return true;
                }
            }
            return false;
        }
    }
}
