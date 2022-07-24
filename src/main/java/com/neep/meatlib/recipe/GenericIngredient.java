package com.neep.meatlib.recipe;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public abstract class GenericIngredient<T, V extends TransferVariant<T>> implements Predicate<StorageView<?>>
{
    protected V resource;
    protected long amount;

    public GenericIngredient(@NotNull V resource, long amount)
    {
        this.resource = resource;
        this.amount = amount;
    }

    public V resource()
    {
        return resource;
    }

    public long amount()
    {
        return amount;
    }

    public T getResourceType()
    {
        return resource.getObject();
    }

    public boolean isBlank()
    {
        return resource.isBlank() || amount <= 0;
    }

    public abstract GenericIngredient<T, V> blank();

    public abstract void write(PacketByteBuf buf);
//    public abstract GenericIngredient<T> read(PacketByteBuf buf);
//    public abstract GenericIngredient<T> read(JsonObject json);

    public long take(Storage<V> storage, TransactionContext context)
    {
        if (this.isBlank() || this.amount <= 0)
        {
            return 0;
        }

        return storage.extract(this.resource, this.amount(), context);
    }

    @Override
    public boolean test(StorageView<?> view)
    {
        return view.getResource().equals(resource) && view.getAmount() >= amount || isBlank();
    }

}
