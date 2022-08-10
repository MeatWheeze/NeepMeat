package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public interface NMREIPlugin
{
    CategoryIdentifier<GrindingDisplay> GRINDING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/grinding");
}
