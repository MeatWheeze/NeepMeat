package com.neep.neepmeat.api;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;

public interface DataVariant extends TransferVariant<DataType>
{
    DataVariant BLANK = of(DataType.BLANK);
    DataVariant NORMAL = of(DataType.NORMAL);
    DataVariant DIVINE = of(DataType.DIVINE);

    static DataVariant fromNbt(NbtCompound nbt)
    {
        return DataVariantImpl.fromNbt(nbt);
    }

    static DataVariant of(DataType data)
    {
        return DataVariantImpl.of(data);
    }

}
