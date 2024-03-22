package com.neep.neepmeat.machine.large_crusher;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class LargeCrusherBlockEntity extends SyncableBlockEntity
{
    private final LargeCrusherStorage storage = new LargeCrusherStorage();

    public LargeCrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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

    public void serverTick()
    {

    }
}
