package com.neep.neepmeat.api;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public interface DataPort
{
//    BlockApiLookup<Storage<DataVariant>, Void> DATA_STORAGE =
//            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "data_storage"),
//                    Storage.asClass(), Void.class);

    BlockApiLookup<DataPort, Void> DATA_PORT =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "data_port"),
                    DataPort.class, Void.class);

    // One GieB (gibi-esoteric byte)
    long GIEB = 1024;

    void setTarget(BlockPos pos);

    long receive(DataVariant variant, long amount, TransactionContext transaction);
}
