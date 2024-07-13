package com.neep.neepmeat.transport.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.client.screen.plc.PLCScreenButton;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.screen_handler.AdvancedRouterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class AdvancedRouterScreen extends BaseHandledScreen<AdvancedRouterScreenHandler>
{
    private Border border = new Border(0, 0, 0, 0, 0, () -> 0);

    public AdvancedRouterScreen(AdvancedRouterScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 17 * 6 + 5;
        this.backgroundHeight = 50;

        super.init();
        this.border = addDrawable(new Border(x, y, backgroundWidth, backgroundHeight, 3, () -> PLCCols.BORDER.col));
        Rectangle withoutPadding = border.withoutPadding();

        for (int i = 0; i < Direction.values().length; ++i)
        {
            Direction direction = Direction.values()[i];
            addDrawableChild(new OpenFilterWidget(withoutPadding.x() + i * 17, withoutPadding.y2() - 16,
                    Text.empty(), direction,
                    this::openFilter, () -> handler.notEmpty(direction)));
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        Rectangle withoutPadding = border.withoutPadding();
        GUIUtil.drawCenteredText(context, textRenderer,
                NeepMeat.translationKey("screen", "advanced_router.choose_side"),
                withoutPadding.x() + withoutPadding.w() / 2f, withoutPadding.y(),
                PLCCols.TEXT.col, true);
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType)
    {
        super.onMouseClick(slot, slotId, button, actionType);
    }

    private void openFilter(Direction direction)
    {
        handler.openFilterC2S.emitter().apply(direction.ordinal());
    }

    private static class OpenFilterWidget extends PLCScreenButton
    {
        private final Direction direction;
        private final Consumer<Direction> onClick;
        private final BooleanSupplier notEmpty;

        public OpenFilterWidget(int x, int y, Text message, Direction direction, Consumer<Direction> onClick, BooleanSupplier notEmpty)
        {
            super(x, y, message);
            this.direction = direction;
            this.onClick = onClick;
            this.notEmpty = notEmpty;
        }

        @Override
        public void renderTooltip(DrawContext matrices, int mouseX, int mouseY)
        {

        }

        @Override
        protected int getU()
        {
            return direction.ordinal() * 16;
        }

        @Override
        protected int getYImage(boolean hovered)
        {
            if (hovered)
                return 2;

            if (!notEmpty.getAsBoolean())
                return 0;

            return 1;
        }

        @Override
        protected int getV()
        {
            return 48;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            super.onClick(mouseX, mouseY);
            onClick.accept(direction);
        }
    }
}
