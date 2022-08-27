package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.*;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlerInit
{
    public static ScreenHandlerType<BufferScreenHandler> BUFFER_SCREEN_HANDLER;
    public static ScreenHandlerType<ContentDetectorScreenHandler> CONTENT_DETECTOR_SCREEN_HANDLER;
    public static ScreenHandlerType<RouterScreenHandler> ROUTER;
    public static ScreenHandlerType<StirlingEngineScreenHandler> STIRLING_ENGINE;
    public static ScreenHandlerType<AlloyKilnScreenHandler> ALLOY_KILN;
    public static ScreenHandlerType<AssemblerScreenHandler> ASSEMBLER;
    public static ScreenHandlerType<WorkstationScreenHandler> WORKSTATION;

    public static void registerScreenHandlers()
    {
        BUFFER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "buffer_screen"), BufferScreenHandler::new);
        CONTENT_DETECTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "content_detector"), ContentDetectorScreenHandler::new);
        ROUTER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "router"), RouterScreenHandler::new);
        STIRLING_ENGINE = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "stirling_engine"), StirlingEngineScreenHandler::new);
        ALLOY_KILN = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "alloy_kiln"), AlloyKilnScreenHandler::new);
        ASSEMBLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "assembler"), AssemblerScreenHandler::new);
        WORKSTATION = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "workstation"), WorkstationScreenHandler::new);
    }
}
