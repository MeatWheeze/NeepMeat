package com.neep.meatlib.api;

import com.google.common.base.Preconditions;
import com.neep.meatlib.transfer.EntityVariant;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.DataStorage;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class EntityStorage
{
    public static final BlockApiLookup<Storage<EntityVariant>, Direction> SIDED =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "sided_entity_storage"), Storage.asClass(), Direction.class);

    static
    {
        DataStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) ->
        {
            Preconditions.checkArgument(!world.isClient(), "Sided data storage may only be queried for a server world.");
            return null;
        });
    }
}
