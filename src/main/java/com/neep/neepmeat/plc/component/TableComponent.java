package com.neep.neepmeat.plc.component;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage")
public interface TableComponent<T>
{
    BlockApiLookup<TableComponent<?>, Void> LOOKUP =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "structure"), asClass(), Void.class);

    Storage<T> getStorage();

    @SuppressWarnings("unchecked")
    static Class<TableComponent<?>> asClass()
    {
        return (Class<TableComponent<?>>) (Object) TableComponent.class;
    }

    Identifier getType();

    default <V, U extends TableComponent<V>> U as()
    {
        return (U) this;
    }
}
