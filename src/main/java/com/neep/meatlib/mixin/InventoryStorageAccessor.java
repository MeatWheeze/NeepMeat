package com.neep.meatlib.mixin;

import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(InventoryStorageImpl.class)
public interface InventoryStorageAccessor
{
    @Accessor("WRAPPERS")
    static Map<Inventory, InventoryStorageImpl> getWRAPPERS()
    {
        throw new AssertionError();
    }
}
