package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.compat.rei.display.*;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class NMCommonPlugin implements REIServerPlugin, NMREIPlugin
{
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry)
    {
        registry.register(GRINDING, GrindingDisplay.serializer(GRINDING));
        registry.register(ADVANCED_CRUSHING, GrindingDisplay.serializer(ADVANCED_CRUSHING));
        registry.register(COMPACTING, CompactingDisplay.serializer());
        registry.register(MIXING, MixingDisplay.serializer());
        registry.register(ALLOY_SMELTING, AlloySmeltingDisplay.serializer());
        registry.register(VIVISECTION, VivisectionDisplay.serializer());
        registry.register(ENLIGHTENING, EnlighteningDisplay.serializer());
        registry.register(PRESSING, PressingDisplay.serializer());
        registry.register(SURGERY, SurgeryDisplay.getSerializer());
        registry.register(MANUFACTURE, ManufactureDisplay.getSerializer());
        registry.register(TRANSFORMING_TOOL, TransformingToolDisplay.serializer());
        registry.register(TROMMEL, TrommelDisplay.serializer());
    }
}
