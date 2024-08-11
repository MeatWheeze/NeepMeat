package com.neep.neepmeat.client.screen.util;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class OldBackground implements Drawable
{
    public static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget/borders.png");

    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final int padding;
    private final Supplier<Integer> col;

    public OldBackground(int x, int y, int w, int h, int padding, Supplier<Integer> col)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.padding = padding;
        this.col = col;
    }

    public OldBackground(Rectangle bounds, int padding, Supplier<Integer> col)
    {
        this(bounds.x(), bounds.y(), bounds.w(), bounds.h(), padding, col);
    }

    public Rectangle withoutPadding()
    {
        return new Rectangle.Immutable(x + padding, y + padding, w - padding * 2, h - padding * 2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        int t = 5; // Border thickness

        // For reasons that I can't be bothered to investigate, it is necessary to extend the width and height by 1
        // to fit all elements.
        int x2 = x + w - t + 1;
        int y2 = y + h - t + 1;
        int a = 1;

        drawTexture(context, x, y, t, t, 0, 0, t, t);
        drawTexture(context, x, y2, t, t, 0, 11, t, t);
        drawTexture(context, x2, y, t, t, 11, 0, t, t);
        drawTexture(context, x2, y2, t, t, 11, 11, t, t);

        drawTexture(context, x + t, y, w - 2 * t + a, t, 5, 0, 1, 5);
        drawTexture(context, x, y + t, t, h - 2 * t + a, 0, 6, 5, 1);
        drawTexture(context, x + t, y2, w - 2 * t + a, t, 6, 11, 1, 5);
        drawTexture(context, x2, y + t, t, h - 2 * t + a, 11, 6, 5, 1);

        drawTexture(context, x + t, y + t, w - 2 * t, h - 2 * t, 7, 7, 1, 1);
    }

    private void drawTexture(DrawContext context, int x, int y, int w, int h, int u, int v, int du, int dv)
    {
        GUIUtil.drawTextureStretch(TEXTURE, context, x, y, w, h, u, v, du, dv, 32, 32);
    }

    public int padding()
    {
        return padding;
    }
}
