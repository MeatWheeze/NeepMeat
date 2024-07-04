package com.neep.neepmeat.item.filter;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public interface Filter extends NbtSerialisable
{
    Registry<Constructor<?>> REGISTRY = FabricRegistryBuilder.<Constructor<?>>createSimple(RegistryKey.ofRegistry(new Identifier(NeepMeat.NAMESPACE, "filter"))).buildAndRegister();

    boolean matches(ItemVariant variant);

    Constructor<?> getType();

    @FunctionalInterface
    interface Constructor<T extends Filter>
    {
        T create();
    }
}
