package com.neep.neepmeat.machine.advanced_integrator;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.DataPort;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SimpleDataPort implements DataPort, NbtSerialisable
{
    @Nullable private LazyBlockApiCache<DataPort, Void> target;

    private final BlockEntity parent;

    public SimpleDataPort(BlockEntity parent)
    {
        this.parent = parent;
    }

    @Override
    public void setTarget(BlockPos target)
    {
        this.target = LazyBlockApiCache.of(DataPort.DATA_PORT, target, parent::getWorld, () -> null);
    }

    @Override
    public long receive(DataVariant variant, long amount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        if (target != null)
        {
            nbt.put("target", NbtHelper.fromBlockPos(target.pos()));
        }
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        if (nbt.contains("target"))
        {
            this.target = LazyBlockApiCache.of(DataPort.DATA_PORT,
                    NbtHelper.toBlockPos(nbt.getCompound("target")),
                    parent::getWorld,
                    () -> null);
        }
    }

    public long send(DataVariant variant, long amount ,TransactionContext transaction)
    {
        if (target != null)
        {
            var found = target.find();
            if (found != null)
            {
                return found.receive(variant, amount, transaction);
            }
        }
        return 0;
    }
}
