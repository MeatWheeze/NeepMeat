package com.neep.meatlib.api;

import com.neep.meatlib.transfer.EntityVariant;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class EntityStorage
{
    public static final BlockApiLookup<Storage<EntityVariant>, Direction> SIDED =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "sided_entity_storage"), Storage.asClass(), Direction.class);
}
