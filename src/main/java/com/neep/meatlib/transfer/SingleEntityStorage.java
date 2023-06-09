package com.neep.meatlib.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class SingleEntityStorage extends SnapshotParticipant<EntityVariant<?>> implements SingleSlotStorage<EntityVariant<?>>
{
    @NotNull  public EntityVariant<?> variant = EntityVariant.getBlank();

    @Override
    public long insert(EntityVariant<?> resource, long maxAmount, TransactionContext transaction)
    {
        if (!isResourceBlank() || maxAmount == 0) return 0;

        maxAmount = Math.min(getCapacity(), maxAmount);

        updateSnapshots(transaction);



        return 0;
    }

    @Override
    public long extract(EntityVariant<?> resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public boolean isResourceBlank()
    {
        return Objects.equals(getResource(), EntityVariant.getBlank());
    }

    @Override
    public EntityVariant<?> getResource()
    {
        return variant;
    }

    @Override
    public long getAmount()
    {
        return isResourceBlank() ? 0 : 1;
    }

    @Override
    public long getCapacity()
    {
        return 1;
    }

    @Override
    protected EntityVariant<?> createSnapshot()
    {
        return getResource();
    }

    @Override
    protected void readSnapshot(EntityVariant<?> snapshot)
    {
        this.variant = snapshot;
    }

    public interface EntityInterface
    {
        void setEntity(Entity entity);

        Entity getEntity();
    }
}
