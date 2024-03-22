package com.neep.neepmeat.machine.grinder;

import com.neep.meatlib.util.NbtSerialisable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;

public interface IGrinderStorage
{
    Storage<ItemVariant> getInputStorage();

    Storage<ItemVariant> getOutputStorage();

    Storage<ItemVariant> getExtraStorage();

    XpStorage getXpStorage();

    class XpStorage extends SnapshotParticipant<Float> implements NbtSerialisable
    {
        private float xp;

        public float insert(float maxAmount, TransactionContext transaction)
        {
            if (maxAmount > 0)
            {
                updateSnapshots(transaction);
                xp += maxAmount;
                return maxAmount;
            }
            return 0;
        }

        public float extract(float maxAmount, TransactionContext transaction)
        {
            if (maxAmount > 0)
            {
                updateSnapshots(transaction);
                float extracted = Math.min(xp, maxAmount);
                xp -= extracted;
                return extracted;
            }
            return 0;
        }

        public float getAmount()
        {
            return xp;
        }

        public NbtCompound writeNbt(NbtCompound nbt)
        {
            nbt.putFloat("amount", xp);
            return nbt;
        }

        public void readNbt(NbtCompound nbt)
        {
            this.xp = nbt.getFloat("amount");
        }

        @Override
        protected Float createSnapshot()
        {
            return xp;
        }

        @Override
        protected void readSnapshot(Float snapshot)
        {
            this.xp = snapshot;
        }
    }
}
