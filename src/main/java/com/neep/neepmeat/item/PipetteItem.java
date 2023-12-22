package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PipetteItem extends BaseItem
{
    protected static ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext> API_PROVIDER = (stack, context) -> new StackStorage(stack, context, FluidConstants.BUCKET);

    public PipetteItem(String registryName, TooltipSupplier tooltip, Settings settings)
    {
        super(registryName, tooltip, settings);
        FluidStorage.ITEM.registerForItems(API_PROVIDER, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();
        if (!world.isClient() && context.getPlayer() != null)
        {
            Storage<FluidVariant> blockStorage = FluidStorage.SIDED.find(world, context.getBlockPos(), context.getSide());
            Storage<FluidVariant> itemStorage = FluidStorage.ITEM.find(context.getStack(), ContainerItemContext.ofPlayerHand(context.getPlayer(), context.getHand()));

            if (context.getPlayer().isSneaking() && itemStorage != null)
            {
                // Clear the storage
                try (Transaction transaction = Transaction.openOuter())
                {
                    FluidVariant resource = StorageUtil.findExtractableResource(itemStorage, transaction);
                    if (resource != null && !resource.isBlank()) itemStorage.extract(resource, Long.MAX_VALUE, transaction);
                    transaction.commit();
                }
            }
            else if (blockStorage != null && itemStorage != null)
            {
                // Transfer to or from block
                if (StorageUtil.move(blockStorage, itemStorage, variant -> true, Long.MAX_VALUE, null) > 0)
                {
                    return ActionResult.SUCCESS;
                }

                if (StorageUtil.move(itemStorage, blockStorage, variant -> true, Long.MAX_VALUE, null) > 0)
                {
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public Text getName(ItemStack stack)
    {
        FluidVariant variant = StackStorage.getResource(stack);
        long amount = StackStorage.getAmount(stack);
        if (amount > 0)
        {
            return new TranslatableText(getTranslationKey() + ".full", FluidVariantAttributes.getName(variant), MiscUtils.dropletsToMb(amount));
        }
        return new TranslatableText(getTranslationKey(stack));
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        return super.getTranslationKey(stack);
    }

    public static class StackStorage extends SnapshotParticipant<NbtCompound> implements SingleSlotStorage<FluidVariant>
    {
        public static final String KEY_ROOT = NeepMeat.NAMESPACE + ":storage";
        public static final String KEY_RESOURCE = "resource";
        public static final String KEY_AMOUNT = "amount";
        public static final String KEY_CAPACITY = "capacity";

        protected final ItemStack stack;
        protected final ContainerItemContext context;
        protected final long capacity;

        public StackStorage(ItemStack stack, ContainerItemContext context, long capacity)
        {
            this.stack = stack;
            this.context = context;
            this.capacity = capacity;
        }

        @Override
        public long insert(FluidVariant insertedResource, long maxAmount, TransactionContext transaction)
        {
            StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);

            long amount = getAmount();
            FluidVariant resource = getResource();
            if ((insertedResource.equals(resource) || resource.isBlank()))
            {
                long insertedAmount = Math.min(maxAmount, getCapacity() - amount);

                if (insertedAmount > 0)
                {
                    updateSnapshots(transaction);

                    if (resource.isBlank())
                    {
                        setResource(insertedResource);
                        setAmount(insertedAmount);
                    }
                    else
                    {
                        setAmount(amount + insertedAmount);
                    }

                    return insertedAmount;
                }
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant extractedResource, long maxAmount, TransactionContext transaction)
        {
            StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);

            FluidVariant resource = getResource();
            long amount = getAmount();

            if (extractedResource.equals(resource))
            {
                long extractedAmount = Math.min(maxAmount, amount);

                if (extractedAmount > 0)
                {
                    updateSnapshots(transaction);
                    setAmount(amount - extractedAmount);

                    if (amount - extractedAmount == 0)
                    {
                        setResource(FluidVariant.blank());
                    }

                    return extractedAmount;
                }
            }

            return 0;
        }

        @Override
        public boolean isResourceBlank()
        {
            return getResource().isBlank();
        }

        @Override
        public FluidVariant getResource()
        {
            return getResource(stack);
        }

        public static FluidVariant getResource(ItemStack stack)
        {
            NbtCompound nbt = stack.getSubNbt(KEY_ROOT);
            if (nbt != null)
            {
                return FluidVariant.fromNbt(nbt.getCompound(KEY_RESOURCE));
            }
            return FluidVariant.blank();
        }

        public void setResource(FluidVariant resource)
        {
            NbtCompound root = stack.getOrCreateSubNbt(KEY_ROOT);
            root.put(KEY_RESOURCE, resource.toNbt());
            stack.setSubNbt(KEY_ROOT, root);
        }

        @Override
        public long getAmount()
        {
            return getAmount(stack);
        }

        public static long getAmount(ItemStack stack)
        {
            NbtCompound nbt = stack.getSubNbt(KEY_ROOT);
            if (nbt != null)
            {
                return nbt.getLong(KEY_AMOUNT);
            }
            return 0;
        }

        public void setAmount(long amount)
        {
            NbtCompound root = stack.getOrCreateSubNbt(KEY_ROOT);
            root.putLong(KEY_AMOUNT, amount);
            stack.setSubNbt(KEY_ROOT, root);
        }

        @Override
        public long getCapacity()
        {
//            NbtCompound nbt = stack.getSubNbt(KEY_ROOT);
//            if (nbt != null)
//            {
//                return nbt.getLong(KEY_CAPACITY);
//            }
            return capacity;
        }

        public void setCapacity(long capacity)
        {
            NbtCompound root = stack.getOrCreateSubNbt(KEY_ROOT);
            root.putLong(KEY_CAPACITY, capacity);
            stack.setSubNbt(KEY_ROOT, root);
        }

        @Override
        protected NbtCompound createSnapshot()
        {
            return stack.getOrCreateNbt().copy();
        }

        @Override
        protected void readSnapshot(NbtCompound snapshot)
        {
            stack.setNbt(snapshot);
        }
    }
}