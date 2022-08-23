package com.neep.neepmeat.machine.integrator;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;

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

    public Storage<ItemVariant> itemStorage = new ItemStorage();

    protected class ItemStorage implements Storage<ItemVariant>
    {
        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
        {
            Integer inc = IntegratorBlockEntity.DATA_MAP.get(resource.getItem());
            if (inc != null)
            {
                float inserted = parent.insertEnlightenment(inc, transaction);
                if (inserted == inc)
                {
                    transaction.addOuterCloseCallback(result ->
                    {
                        parent.getWorld().playSound(null, parent.getPos(), SoundEvents.ENTITY_HORSE_EAT, SoundCategory.BLOCKS, 1, 1);
                    });
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        public Iterator<? extends StorageView<ItemVariant>> iterator(TransactionContext transaction)
        {
            return Collections.emptyIterator();
        }
    }

    protected static class ImmatureStorage extends SingleVariantStorage<FluidVariant> implements InsertionOnlyStorage<FluidVariant>, SingleSlotStorage<FluidVariant>
    {
        protected long capacity = FluidConstants.BUCKET;

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

    }
}
