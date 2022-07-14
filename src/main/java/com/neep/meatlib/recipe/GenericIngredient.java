package com.neep.meatlib.recipe;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public abstract class GenericIngredient<T>
{
    protected TransferVariant<T> resource;
    protected long amount;

    public GenericIngredient(@NotNull TransferVariant<T> resource, long amount)
    {
        this.resource = resource;
        this.amount = amount;
    }

    public TransferVariant<T> resource()
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

    public abstract GenericIngredient<T> blank();

    public abstract void write(PacketByteBuf buf);
//    public abstract GenericIngredient<T> read(PacketByteBuf buf);
//    public abstract GenericIngredient<T> read(JsonObject json);

    public static void processIngrediend(JsonElement json)
    {

    }
}
