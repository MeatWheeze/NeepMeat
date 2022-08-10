package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class NMCommonPlugin implements REIServerPlugin, NMREIPlugin
{
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry)
    {
        registry.register(NMREIPlugin.GRINDING, GrindingDisplay.serializer());
    }
}
