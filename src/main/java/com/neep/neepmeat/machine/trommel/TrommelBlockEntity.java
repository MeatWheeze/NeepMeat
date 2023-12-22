package com.neep.neepmeat.machine.trommel;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class TrommelBlockEntity extends SyncableBlockEntity
{
    protected TrommelStorage storage;

    public TrommelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storage = new TrommelStorage(this);
    }

    public TrommelBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TROMMEL, pos, state);
    }

    public TrommelStorage getStorage()
    {
        return storage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }
}
