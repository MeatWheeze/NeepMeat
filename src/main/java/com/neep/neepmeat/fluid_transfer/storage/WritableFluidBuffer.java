package com.neep.neepmeat.fluid_transfer.storage;

import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class WritableFluidBuffer extends SingleVariantStorage<FluidVariant> implements FluidBuffer
{
    protected long capacity;
    private final BlockEntity parent;

    public WritableFluidBuffer(@Nullable BlockEntity parent, long capacity)
    {
        this.capacity = capacity;
        this.parent = parent;
    }

    public NbtCompound writeNBT(NbtCompound nbt)
    {
        nbt.putLong("amount", amount);
        nbt.put("resource", variant.toNbt());

        return nbt;
    }

    public void readNBT(NbtCompound nbt)
    {
        this.amount = nbt.getLong("amount");
        this.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("resource"));
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
    @Override
    protected void onFinalCommit()
    {
        if (this.amount <= 0)
        {
//            this.variant = getBlankVariant();
        }
        syncIfPossible();
    }

    public boolean handleInteract(PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
        if (storage != null)
        {
            if (StorageUtil.move(storage, this, variant -> true, Long.MAX_VALUE, null) > 0)
                return true;

            if (StorageUtil.move(this, storage, variant -> true, Long.MAX_VALUE, null) > 0)
                return true;
        }
        return false;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
        syncIfPossible();
    }

    public void syncIfPossible()
    {
        if (parent != null)
        {
            parent.markDirty();
        }
        if (parent instanceof BlockEntityClientSerializable serializable)
        {
            serializable.sync();
        }
    }
}
