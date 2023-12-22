package com.neep.neepmeat.recipe.surgery;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public interface TableComponent<T>
{
    BlockApiLookup<TableComponent<?>, Void> STRUCTURE_LOOKUP =
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
