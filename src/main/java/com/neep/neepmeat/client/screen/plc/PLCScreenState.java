package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.plc.instruction.Argument;

public interface PLCScreenState
{
    void argument(Argument argument);

    void onKeyPressed(int keyCode, int scanCode, int modifiers);

    boolean isSelected();
}
