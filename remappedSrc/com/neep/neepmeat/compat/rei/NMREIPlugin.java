package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.display.*;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public interface NMREIPlugin
{
    CategoryIdentifier<GrindingDisplay> GRINDING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/grinding");
    CategoryIdentifier<CompactingDisplay> COMPACTING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/compacting");
    CategoryIdentifier<MixingDisplay> MIXING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/mixing");
    CategoryIdentifier<AlloySmeltingDisplay> ALLOY_SMELTING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/alloy_smelting");
    CategoryIdentifier<HeartExtractionDisplay> HEART_EXTRACTION = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/heart_extraction");
    CategoryIdentifier<EnlighteningDisplay> ENLIGHTENING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/enlightening");
    CategoryIdentifier<PressingDisplay> PRESSING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/pressing");
    CategoryIdentifier<SurgeryDisplay> SURGERY = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/surgery");

    CategoryIdentifier<TrommelDisplay> TROMMEL = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/trommel");
    CategoryIdentifier<HeatingDisplay> HEATING = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/heating");
    CategoryIdentifier<TransformingToolDisplay> TRANSFORMING_TOOL = CategoryIdentifier.of(NeepMeat.NAMESPACE, "plugins/transforming_tool");
}