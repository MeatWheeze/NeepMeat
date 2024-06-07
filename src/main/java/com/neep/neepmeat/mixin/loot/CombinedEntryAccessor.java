package com.neep.neepmeat.mixin.loot;

import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Deep, deep jank
@Mixin(CombinedEntry.class)
public interface CombinedEntryAccessor
{
    @Accessor
    LootPoolEntry[] getChildren();
}
