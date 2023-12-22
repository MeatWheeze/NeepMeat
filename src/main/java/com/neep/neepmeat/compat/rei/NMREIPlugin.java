package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.display.CompactingDisplay;
import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import com.neep.neepmeat.compat.rei.display.MixingDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public interface NMREIPlugin
{
    CategoryIdentifier<GrindingDisplay> GRINDING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/grinding");
    CategoryIdentifier<CompactingDisplay> COMPACTING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/compacting");
    CategoryIdentifier<MixingDisplay> MIXING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/mixing");
}
