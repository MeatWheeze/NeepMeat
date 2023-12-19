package com.neep.neepmeat.api.plc;

import software.bernie.geckolib3.core.util.Color;

public enum PLCCols
{
    INVALID(Color.ofRGBA(100, 30, 30, 255).getColor()),
    BORDER(Color.ofRGBA(255, 94, 33, 255).getColor()),
    TEXT(Color.ofRGBA(255, 94, 33, 255).getColor()),
    SELECTED(Color.ofRGBA(255, 150, 33, 255).getColor()),
    TRANSPARENT(Color.ofRGBA(255, 94, 33, 100).getColor());

    PLCCols(int col)
    {
        this.col = col;
    }

    public final int col;
}
