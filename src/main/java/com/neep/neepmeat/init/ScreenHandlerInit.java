package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import com.neep.neepmeat.screen_handler.*;
import com.neep.neepmeat.transport.screen_handler.TransportScreenHandlers;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScreenHandlerInit
{
    public static ScreenHandlerType<BufferScreenHandler> BUFFER_SCREEN_HANDLER;
    public static ScreenHandlerType<ContentDetectorScreenHandler> CONTENT_DETECTOR_SCREEN_HANDLER;
    public static ScreenHandlerType<RouterScreenHandler> ROUTER;
    public static ScreenHandlerType<StirlingEngineScreenHandler> STIRLING_ENGINE;
    public static ScreenHandlerType<AlloyKilnScreenHandler> ALLOY_KILN;
    public static ScreenHandlerType<AssemblerScreenHandler> ASSEMBLER;
    public static ScreenHandlerType<WorkstationScreenHandler> WORKSTATION;
    public static ScreenHandlerType<GuideScreenHandler> GUIDE;
    public static ScreenHandlerType<UpgradeManagerScreenHandler> UPGRADE_MANAGER;
    public static ExtendedScreenHandlerType<FluidRationerScreenHandler> FLUID_RATIONER = new ExtendedScreenHandlerType<>(FluidRationerScreenHandler::new);

    public static ScreenHandlerType<PLCScreenHandler> PLC;

    public static void registerScreenHandlers()
    {
        BUFFER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "buffer_screen"), BufferScreenHandler::new);
        CONTENT_DETECTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "content_detector"), ContentDetectorScreenHandler::new);
        ROUTER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "router"), RouterScreenHandler::new);
        STIRLING_ENGINE = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "stirling_engine"), StirlingEngineScreenHandler::new);
        ALLOY_KILN = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "alloy_kiln"), AlloyKilnScreenHandler::new);
        ASSEMBLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "assembler"), AssemblerScreenHandler::new);
        WORKSTATION = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "workstation"), WorkstationScreenHandler::new);
        GUIDE = register(NeepMeat.NAMESPACE, "guide", GuideScreenHandler::new);
        UPGRADE_MANAGER = register(NeepMeat.NAMESPACE, "upgrade_manager", UpgradeManagerScreenHandler::new);
        FLUID_RATIONER = Registry.register(Registry.SCREEN_HANDLER, new Identifier(NeepMeat.NAMESPACE, "fluid_rationer"), FLUID_RATIONER);

        PLC = registerExtended(NeepMeat.NAMESPACE, "plc", PLCScreenHandler::new);

        TransportScreenHandlers.registerScreenHandlers();
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String namespace, String id, ScreenHandlerType.Factory<T> factory)
    {
        return Registry.register(Registry.SCREEN_HANDLER, new Identifier(namespace, id), new ScreenHandlerType<>(factory));
    }

    public static <T extends ScreenHandler> ExtendedScreenHandlerType<T> registerExtended(String namespace, String id, ExtendedScreenHandlerType.ExtendedFactory<T> factory)
    {
        return Registry.register(Registry.SCREEN_HANDLER, new Identifier(namespace, id), new ExtendedScreenHandlerType<>(factory));
    }
}
