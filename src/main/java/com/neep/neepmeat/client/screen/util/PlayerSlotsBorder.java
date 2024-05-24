package com.neep.neepmeat.client.screen.util;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

import java.util.function.Supplier;

public class PlayerSlotsBorder implements Drawable
{
    private final int x;
    private final int y;
    private final int w = BasicScreenHandler.playerSlotsW();
    private final int h = BasicScreenHandler.playerInvH();

    protected final Supplier<Integer> col;

    public PlayerSlotsBorder(int x, int y, Supplier<Integer> col)
    {
        this.x = x;
        this.y = y;
        this.col = col;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        GUIUtil.renderBorder(context, x, y, w - 1, h - 1 - 18 - 4, col.get(), 0);
        GUIUtil.renderBorder(context, x, y, w - 1, h - 1 - 18 - 4, PLCCols.TRANSPARENT.col, -1);

        GUIUtil.renderBorder(context, x, y + 58, w - 1, 18 + 1, col.get(), 0);
        GUIUtil.renderBorder(context, x, y + 58, w - 1, 18 + 1, PLCCols.TRANSPARENT.col, -1);
    }
}
