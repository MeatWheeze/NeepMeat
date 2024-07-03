package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.item.filter.TagFilter;

public class TagFilterWidget extends FilterScreen.FilterEntryWidget
{
    private final TagFilter filter;

    public TagFilterWidget(int w, int index, TagFilter filter)
    {
        super(w, index);
        this.filter = filter;
    }

    @Override
    public void init()
    {
        super.init();
    }
}
