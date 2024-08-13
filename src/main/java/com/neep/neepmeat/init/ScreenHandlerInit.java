package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.fabricator.FabricatorScreenHandler;
import com.neep.neepmeat.machine.small_compressor.SmallCompressorScreenHandler;
import com.neep.neepmeat.neepbus.screen.NeepBusRangeConfigScreenHandler;
import com.neep.neepmeat.neepbus.screen.SliderScreenHandler;
import com.neep.neepmeat.neepbus.screen.NeepBusConfigScreenHandler;
import com.neep.neepmeat.screen_handler.DisplayPlateScreenHandler;
import com.neep.neepmeat.machine.separator.SeparatorScreenHandler;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import com.neep.neepmeat.screen_handler.*;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import com.neep.neepmeat.transport.screen_handler.TransportScreenHandlers;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlerInit
{
    public static ScreenHandlerType<BufferScreenHandler> BUFFER_SCREEN_HANDLER;
    public static ScreenHandlerType<ContentDetectorScreenHandler> CONTENT_DETECTOR_SCREEN_HANDLER;
    public static ScreenHandlerType<RouterScreenHandler> ROUTER;
    public static ScreenHandlerType<AdvancedRouterScreenHandler> ADVANCED_ROUTER;
    public static ScreenHandlerType<StirlingEngineScreenHandler> STIRLING_ENGINE;
    public static ScreenHandlerType<AlloyKilnScreenHandler> ALLOY_KILN;
    public static ScreenHandlerType<AssemblerScreenHandler> ASSEMBLER;
    public static ScreenHandlerType<WorkstationScreenHandler> WORKSTATION;
    public static ScreenHandlerType<GuideScreenHandler> GUIDE;
    public static ScreenHandlerType<UpgradeManagerScreenHandler> UPGRADE_MANAGER;
    public static ExtendedScreenHandlerType<FluidRationerScreenHandler> FLUID_RATIONER = new ExtendedScreenHandlerType<>(FluidRationerScreenHandler::new);
    public static ExtendedScreenHandlerType<SeparatorScreenHandler> SEPARATOR = new ExtendedScreenHandlerType<>(SeparatorScreenHandler::new);
    public static ExtendedScreenHandlerType<DisplayPlateScreenHandler> DISPLAY_PLATE;
    public static ScreenHandlerType<FabricatorScreenHandler> FABRICATOR;
    public static ScreenHandlerType<SmallCompressorScreenHandler> SMALL_COMPRESSOR;

    public static ScreenHandlerType<ItemOutputScreenHandler> ITEM_OUTPUT;

    public static ScreenHandlerType<FilterScreenHandler> FILTER;

    public static ExtendedScreenHandlerType<NeepBusConfigScreenHandler> NEEPBUS_CONFIG;
    public static ExtendedScreenHandlerType<NeepBusRangeConfigScreenHandler> NEEPBUS_RANGE_CONFIG;
    public static ScreenHandlerType<SliderScreenHandler> SLIDER;

    public static ScreenHandlerType<PLCScreenHandler> PLC;
    public static ScreenHandlerType<LivingMachineScreenHandler> LIVING_MACHINE;

    public static void registerScreenHandlers()
    {
        BUFFER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "buffer_screen"), BufferScreenHandler::new);
        CONTENT_DETECTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "content_detector"), ContentDetectorScreenHandler::new);
        ROUTER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "router"), RouterScreenHandler::new);
        ADVANCED_ROUTER = register(NeepMeat.NAMESPACE, "advanced_router", AdvancedRouterScreenHandler::new);
        STIRLING_ENGINE = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "stirling_engine"), StirlingEngineScreenHandler::new);
        ALLOY_KILN = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "alloy_kiln"), AlloyKilnScreenHandler::new);
        ASSEMBLER = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "assembler"), AssemblerScreenHandler::new);
        WORKSTATION = ScreenHandlerRegistry.registerSimple(new Identifier(NeepMeat.NAMESPACE, "workstation"), WorkstationScreenHandler::new);
        GUIDE = register(NeepMeat.NAMESPACE, "guide", GuideScreenHandler::new);
        UPGRADE_MANAGER = registerExtended(NeepMeat.NAMESPACE, "upgrade_manager", UpgradeManagerScreenHandler::new);
        FLUID_RATIONER = Registry.register(Registries.SCREEN_HANDLER, new Identifier(NeepMeat.NAMESPACE, "fluid_rationer"), FLUID_RATIONER);
        SEPARATOR = Registry.register(Registries.SCREEN_HANDLER, new Identifier(NeepMeat.NAMESPACE, "separator"), SEPARATOR);
        DISPLAY_PLATE = registerExtended(NeepMeat.NAMESPACE, "display_plate", DisplayPlateScreenHandler::new);
        FABRICATOR = registerExtended(NeepMeat.NAMESPACE, "fabricator", FabricatorScreenHandler::new);
        SMALL_COMPRESSOR = register(NeepMeat.NAMESPACE, "small_compressor", SmallCompressorScreenHandler::new);
        FILTER = register(NeepMeat.NAMESPACE, "filter", FilterScreenHandler::new);

        NEEPBUS_CONFIG = registerExtended(NeepMeat.NAMESPACE, "neepbus_config", NeepBusConfigScreenHandler::new);
        NEEPBUS_RANGE_CONFIG = registerExtended(NeepMeat.NAMESPACE, "neepbus_range_config", NeepBusRangeConfigScreenHandler::new);
        SLIDER = register(NeepMeat.NAMESPACE, "neepbus_slider", SliderScreenHandler::new);

        LIVING_MACHINE = registerExtended(NeepMeat.NAMESPACE, "living_machine", LivingMachineScreenHandler::new);
        ITEM_OUTPUT = register(NeepMeat.NAMESPACE, "item_output", ItemOutputScreenHandler::new);

        PLC = registerExtended(NeepMeat.NAMESPACE, "plc", PLCScreenHandler::new);

        TransportScreenHandlers.registerScreenHandlers();
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String namespace, String id, ScreenHandlerType.Factory<T> factory)
    {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(namespace, id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static <T extends ScreenHandler> ExtendedScreenHandlerType<T> registerExtended(String namespace, String id, ExtendedScreenHandlerType.ExtendedFactory<T> factory)
    {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(namespace, id), new ExtendedScreenHandlerType<>(factory));
    }
}
