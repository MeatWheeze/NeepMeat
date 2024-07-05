package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.item.filter.TagFilter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;

public class TagFilterWidget extends FilterEntryWidget<TagFilter>
{
    public TagFilterWidget(int w, int index, TagFilter filter, FilterScreenHandler handler)
    {
        super(w, 41, index, filter, handler);
    }

    @Override
    public void init()
    {
        super.init();
    }
}
