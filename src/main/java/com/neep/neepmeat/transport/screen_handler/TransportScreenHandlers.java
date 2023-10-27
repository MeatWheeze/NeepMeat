package com.neep.neepmeat.transport.screen_handler;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.util.Identifier;

import static net.minecraft.registry.Registries.SCREEN_HANDLER;
import static net.minecraft.registry.Registry.register;

public class TransportScreenHandlers
{
    public static ExtendedScreenHandlerType<LimiterValveScreenHandler> LIMITER_VALVE = new ExtendedScreenHandlerType<>(LimiterValveScreenHandler::new);
    public static ExtendedScreenHandlerType<ItemRequesterScreenHandler> ITEM_REQUESTER_HANDLER = new ExtendedScreenHandlerType<>(ItemRequesterScreenHandler::new);

    public static void registerScreenHandlers()
    {
        LIMITER_VALVE = register(SCREEN_HANDLER, new Identifier(NeepMeat.NAMESPACE, "limiter_valve"), LIMITER_VALVE);
    }

}
