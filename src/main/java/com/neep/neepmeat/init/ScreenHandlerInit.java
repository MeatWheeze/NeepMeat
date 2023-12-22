package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.BufferScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlerInit
{
    public static ScreenHandlerType<BufferScreenHandler> BUFFER_SCREEN_HANDLER;

    public static void registerScreenHandlers()
    {
        BUFFER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "buffer_screen"), BufferScreenHandler::new);
    }
}
