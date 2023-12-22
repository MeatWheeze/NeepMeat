package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.compat.rei.display.*;
import dev.architectury.event.events.common.ExplosionEvent;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class NMCommonPlugin implements REIServerPlugin, NMREIPlugin
{
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry)
    {
        registry.register(GRINDING, GrindingDisplay.serializer());
        registry.register(COMPACTING, CompactingDisplay.serializer());
        registry.register(MIXING, MixingDisplay.serializer());
        registry.register(ALLOY_SMELTING, AlloySmeltingDisplay.serializer());
        registry.register(HEART_EXTRACTION, HeartExtractionDisplay.serializer());
        registry.register(ENLIGHTENING, EnlighteningDisplay.serializer());
        registry.register(PRESSING, PressingDisplay.serializer());
    }
}
