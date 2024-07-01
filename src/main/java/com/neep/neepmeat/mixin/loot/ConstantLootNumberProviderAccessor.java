package com.neep.neepmeat.mixin.loot;

import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConstantLootNumberProvider.class)
public interface ConstantLootNumberProviderAccessor
{
    @Accessor
    float getValue();
}
