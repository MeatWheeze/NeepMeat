package com.neep.neepmeat.transport.screen_handler;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.minecraft.util.registry.Registry.register;

public class TransportScreenHandlers
{
    public static ExtendedScreenHandlerType<LimiterValveScreenHandler> LIMITER_VALVE = new ExtendedScreenHandlerType<>(LimiterValveScreenHandler::new);
    public static ExtendedScreenHandlerType<ItemRequesterScreenHandler> ITEM_REQUESTER_HANDLER = new ExtendedScreenHandlerType<>(ItemRequesterScreenHandler::new);
    public static ExtendedScreenHandlerType<VSCScreenHandler> VSC = new ExtendedScreenHandlerType<>(VSCScreenHandler::new);

    public static void registerScreenHandlers()
    {
        LIMITER_VALVE = register(Registry.SCREEN_HANDLER, new Identifier(NeepMeat.NAMESPACE, "limiter_valve"), LIMITER_VALVE);
        VSC = register(Registry.SCREEN_HANDLER, new Identifier(NeepMeat.NAMESPACE, "vsc"), VSC);
    }
}
