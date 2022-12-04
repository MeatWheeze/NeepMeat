package com.neep.neepmeat.recipe.surgery;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface TableComponent<T>
{
    BlockApiLookup<TableComponent, Void> STRUCTURE_LOOKUP =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "structure"), TableComponent.class, Void.class);

    BlockApiLookup<Storage<T>, Direction> getSidedLookup();

    Storage<T> getStorage();
}
