package com.neep.neepmeat.blockentity.integrator;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class IntegratorStorage
{
    protected IntegratorBlockEntity parent;

    public IntegratorStorage(IntegratorBlockEntity parent)
    {
        this.parent = parent;
    }

    protected ImmatureStorage immatureStorage = new ImmatureStorage();

    public Storage<FluidVariant> getFluidStorage(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (!parent.isMature)
        {
            return immatureStorage;
        }
        return null;
    }

//    public static final String NBT_VARIANT = "variant";
//    public static final String NBT_AMOUNT = "amount";
    public static final String NBT_IMMATURE_STORAGE = "variant";

    public void writeNbt(NbtCompound nbt)
    {
        NbtCompound immature = new NbtCompound();
        immatureStorage.writeNbt(immature);
        nbt.put(NBT_IMMATURE_STORAGE, immature);
    }

    public void readNbt(NbtCompound nbt)
    {
        NbtCompound immature = nbt.getCompound(NBT_IMMATURE_STORAGE);
        immatureStorage.readNbt(immature);
    }

    protected static class ImmatureStorage extends SingleVariantStorage<FluidVariant> implements InsertionOnlyStorage<FluidVariant>, SingleSlotStorage<FluidVariant>
    {
        protected long capacity = FluidConstants.BUCKET;

        public long grow(long maxAmount, TransactionContext transaction)
        {
//            long amountExtracted = Math.max(0, amount - maxAmount);
//            updateSnapshots(transaction);
//            amount -= amountExtracted;
            return this.extract(getResource(), maxAmount, transaction);
        }

        @Override
        protected FluidVariant getBlankVariant()
        {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant)
        {
            return capacity;
        }

        public void writeNbt(NbtCompound nbt)
        {
            nbt.putLong("amount", amount);
            nbt.put("resource", variant.toNbt());
        }

        public void readNbt(NbtCompound nbt)
        {
            this.amount = nbt.getLong("amount");
            this.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("resource"));
        }

//        @Override
//        public boolean isResourceBlank()
//        {
//            return resource.isBlank();
//        }
//
//        @Override
//        public FluidVariant getResource()
//        {
//            return resource;
//        }
//
//        @Override
//        public long getAmount()
//        {
//            return amount;
//        }
//
//        @Override
//        public long getCapacity()
//        {
//            return capacity;
//        }
//
//        @Override
//        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
//        {
//            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
//
//            if (resource.equals(this.resource))
//            {
//                long amountInserted = Math.min(amount + maxAmount, capacity);
//                if (amountInserted > 0)
//                {
//                    updateSnapshots(transaction);
//                    this.amount += amountInserted;
//                    return amountInserted;
//                }
//            }
//            return 0;
//        }
//
//        @Override
//        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
//        {
//            return 0;
//        }
//
//        @Override
//        protected ResourceAmount<FluidVariant> createSnapshot()
//        {
//            return new ResourceAmount<>(resource, amount);
//        }
//
//        @Override
//        protected void readSnapshot(ResourceAmount<FluidVariant> snapshot)
//        {
//            this.amount = snapshot.amount();
//            this.resource = snapshot.resource();
//        }
    }

}
