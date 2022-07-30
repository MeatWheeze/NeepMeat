package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.BufferScreenHandler;
import com.neep.neepmeat.screen_handler.ContentDetectorScreenHandler;
import com.neep.neepmeat.screen_handler.RouterScreenHandler;
import com.neep.neepmeat.screen_handler.StirlingEngineScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlerInit
{
    public static ScreenHandlerType<BufferScreenHandler> BUFFER_SCREEN_HANDLER;
    public static ScreenHandlerType<ContentDetectorScreenHandler> CONTENT_DETECTOR_SCREEN_HANDLER;
    public static ScreenHandlerType<RouterScreenHandler> ROUTER;
    public static ScreenHandlerType<StirlingEngineScreenHandler> STIRLING_ENGINE;

    public static void registerScreenHandlers()
    {
        BUFFER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "buffer_screen"), BufferScreenHandler::new);
        CONTENT_DETECTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "content_detector"), ContentDetectorScreenHandler::new);
        ROUTER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "router"), RouterScreenHandler::new);
        STIRLING_ENGINE = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "stirling_engine"), StirlingEngineScreenHandler::new);
    }
}
