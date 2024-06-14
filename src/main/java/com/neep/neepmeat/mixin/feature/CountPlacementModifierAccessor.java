package com.neep.neepmeat.mixin.feature;

import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CountPlacementModifier.class)
public interface CountPlacementModifierAccessor
{
    @Accessor
    IntProvider getCount();
}
