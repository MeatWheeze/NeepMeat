package com.neep.neepmeat.compat.kubejs;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.event.EventHandler;

public class NMKJSServerEvents
{
    EventHandler MEATLIB_RECIPES = ServerEvents.GROUP.server("meatlibRecipes", () -> MeatlibRecipesEventJS.class);
}
