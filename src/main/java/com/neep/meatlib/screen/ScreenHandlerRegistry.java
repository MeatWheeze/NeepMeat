package com.neep.meatlib.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlerRegistry
{
    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String namespace, String id, ScreenHandlerType.Factory<T> factory)
    {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(namespace, id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static <T extends ScreenHandler> ExtendedScreenHandlerType<T> registerExtended(String namespace, String id, ExtendedScreenHandlerType.ExtendedFactory<T> factory)
    {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(namespace, id), new ExtendedScreenHandlerType<>(factory));
    }
}
