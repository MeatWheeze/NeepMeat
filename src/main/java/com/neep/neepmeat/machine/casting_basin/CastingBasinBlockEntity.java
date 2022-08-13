package com.neep.neepmeat.machine.casting_basin;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CastingBasinBlockEntity extends SyncableBlockEntity
{
    protected CastingBasinStorage storage;

    public CastingBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new CastingBasinStorage(this);
    }

    public CastingBasinBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.CASTING_BASIN, pos, state);
    }

    public CastingBasinStorage getStorage()
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
