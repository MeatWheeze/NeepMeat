package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.compat.rei.display.AlloySmeltingDisplay;
import com.neep.neepmeat.compat.rei.display.CompactingDisplay;
import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import com.neep.neepmeat.compat.rei.display.MixingDisplay;
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
    }
}
