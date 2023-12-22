package com.neep.neepmeat.block.content_detector;

import net.minecraft.screen.PropertyDelegate;

public class ContentDetectorBehaviour
{
    public static final int DEL_COUNT = 0;
    public static final int DEL_BEHAVIOUR = 1;

    public int value;
    public int maxValue;

    public String tagName;

    public ContentDetectorBehaviour(int maxValue, String tagName)
    {
        this.maxValue = maxValue;
        this.tagName = tagName;
        this.value = 0;
    }

    public static void cycleDelegate(int index, PropertyDelegate delegate)
    {
        int old = delegate.get(index);
        int maxValue;
        if (index == DEL_COUNT)
        {
            maxValue = 3;
        }
        else if (index == DEL_BEHAVIOUR)
        {
            maxValue = 1;
        }
        else
        {
            return;
        }

        delegate.set(index, (old + 1) % (maxValue + 1));
    }
}
