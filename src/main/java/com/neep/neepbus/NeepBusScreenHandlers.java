package com.neep.neepbus;

import com.neep.neepbus.screen.NeepBusConfigScreenHandler;
import com.neep.neepbus.screen.NeepBusRangeConfigScreenHandler;
import com.neep.neepbus.screen.SliderScreenHandler;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandlerType;

import static com.neep.meatlib.screen.ScreenHandlerRegistry.registerExtended;
import static com.neep.meatlib.screen.ScreenHandlerRegistry.register;

public class NeepBusScreenHandlers
{
    public static ExtendedScreenHandlerType<NeepBusConfigScreenHandler> NEEPBUS_CONFIG;
    public static ExtendedScreenHandlerType<NeepBusRangeConfigScreenHandler> NEEPBUS_RANGE_CONFIG;
    public static ScreenHandlerType<SliderScreenHandler> SLIDER;

    public static void init()
    {
        NEEPBUS_CONFIG = registerExtended(NeepMeat.NAMESPACE, "neepbus_config", NeepBusConfigScreenHandler::new);
        NEEPBUS_RANGE_CONFIG = registerExtended(NeepMeat.NAMESPACE, "neepbus_range_config", NeepBusRangeConfigScreenHandler::new);
        SLIDER = register(NeepMeat.NAMESPACE, "neepbus_slider", SliderScreenHandler::new);
    }
}
