package com.neep.meatlib.recipe.ingredient;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;

import java.util.function.BiFunction;

public interface RecipeOutput<T>
{
    T resource();
    long amount();
    long maxAmount();
    long minAmount();
    float chance();
    void update();
    void setNbt(NbtCompound nbt);
    <V extends TransferVariant<T>> boolean insertInto(Storage<V> storage, BiFunction<T, NbtCompound, V> of, TransactionContext transaction);

    void write(Registry<T> registry, PacketByteBuf buf);

    RecipeOutput<Object> EMPTY = new RecipeOutput<>()
    {
        @Override
        public Object resource()
        {
            return null;
        }

        @Override
        public long amount()
        {
            return 0;
        }

        @Override
        public long maxAmount()
        {
            return 0;
        }

        @Override
        public long minAmount()
        {
            return 0;
        }

        @Override
        public float chance()
        {
            return 0;
        }

        @Override
        public void update()
        {

        }

        @Override
        public void setNbt(NbtCompound nbt)
        {

        }

        @Override
        public <V extends TransferVariant<Object>> boolean insertInto(Storage<V> storage, BiFunction<Object, NbtCompound, V> of, TransactionContext transaction)
        {
            return true;
        }

        @Override
        public void write(Registry<Object> registry, PacketByteBuf buf)
        {

        }
    };

    static <T> RecipeOutput<T> empty()
    {
        return (RecipeOutput<T>) EMPTY;
    }
}
