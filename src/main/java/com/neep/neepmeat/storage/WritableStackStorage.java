package com.neep.neepmeat.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

@SuppressWarnings("UnstableApiUsage")
public class WritableStackStorage extends SingleVariantStorage<ItemVariant>
{
    protected int capacity = 64;
    protected BlockEntity parent;

    public WritableStackStorage(@Nullable BlockEntity parent)
    {
        this.parent = parent;
    }

    @Override
    protected void onFinalCommit()
    {
        syncIfPossible();
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putLong("amount", getAmount());
        nbt.put("resource", getResource().toNbt());
//        System.out.println("writing " + getResource() + " =------------------------------------------");
        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        this.amount = nbt.getLong("amount");
        this.variant = ItemVariant.fromNbt((NbtCompound) nbt.get("resource"));
//        System.out.println("reading " + " =------------ ------------------------------");
    }

    public void syncIfPossible()
    {
        if (parent != null)
        {
            parent.markDirty();
            parent.getWorld().updateListeners(parent.getPos(), parent.getCachedState(), parent.getCachedState(), Block.NOTIFY_LISTENERS);

//            if (parent instanceof BlockEntityClientSerializable becs)
//            {
//                becs.sync();
//            }
//            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    public void setStack(ItemStack stack)
    {
        this.variant = ItemVariant.of(stack);
        this.amount = stack.getCount();
        syncIfPossible();
    }

    public ItemStack getAsStack()
    {
        return variant.toStack((int) getAmount());
    }

    @Override
    protected ItemVariant getBlankVariant()
    {
        return ItemVariant.blank();
    }

    @Override
    protected long getCapacity(ItemVariant variant)
    {
        return capacity;
    }
}
