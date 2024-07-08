package com.neep.neepmeat.item.filter;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface Filter extends NbtSerialisable
{
    Registry<Constructor<?>> REGISTRY = MeatLib.<Constructor<?>>createSimple(RegistryKey.ofRegistry(new Identifier(NeepMeat.NAMESPACE, "filter"))).buildAndRegister();

    boolean matches(ItemVariant variant);

    Constructor<?> getType();

    @FunctionalInterface
    interface Constructor<T extends Filter>
    {
        T create();
    }
}
