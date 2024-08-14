package com.neep.neepbus.screen;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;

public interface SimpleScreenHandlerFactory extends NamedScreenHandlerFactory
{
    @Override
    default Text getDisplayName() { return Text.empty(); }
}
