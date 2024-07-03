package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.item.filter.ItemFilter;
import net.minecraft.client.gui.DrawContext;

public class ItemFilterWidget extends FilterScreen.FilterEntryWidget
{
    private final ItemFilter filter;

    public ItemFilterWidget(int w, int index, ItemFilter filter)
    {
        super(w, index);
        this.filter = filter;
    }

    @Override
    public void init()
    {
        super.init();
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);


    }
}
