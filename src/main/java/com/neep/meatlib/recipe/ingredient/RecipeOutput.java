package com.neep.meatlib.recipe.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.BiFunction;

@SuppressWarnings("UnstableApiUsage")
public class RecipeOutput<T>
{
    protected final T resource;
    protected final UniformIntProvider lootFunction;
    protected final Random random;
    protected final float chance;
    protected int amount;
    protected boolean willOutput;
    protected @Nullable NbtCompound nbt;

    public RecipeOutput(@NotNull T resource, int min, int max, float probability)
    {
        this.resource = resource;
        this.lootFunction = UniformIntProvider.create(min, max);
        this.random = new Random();
        this.chance = probability;
    }

    public RecipeOutput(@NotNull T resource, int min, int max)
    {
        this(resource, min, max, 1);
    }

    public T resource()
    {
        return resource;
    }

    public long amount()
    {
        return amount;
    }

    public long maxAmount()
    {
        return lootFunction.getMax();
    }

    public long minAmount()
    {
        return lootFunction.getMin();
    }

    public float chance()
    {
        return chance;
    }

    public void update()
    {
        amount = lootFunction.get(random);
        willOutput = random.nextFloat() < chance;
    }

//    public <V extends TransferVariant<T>> boolean insertInto(Storage<V> storage, Function<T, V> of, TransactionContext transaction)
//    {
//        update();
//        if (!willOutput)
//            return true;
//
//        V variant = of.apply(resource).
//        long inserted = storage.insert(of.apply(resource()), amount, transaction);
//        return inserted == amount;
//    }

    public void setNbt(NbtCompound nbt)
    {
        this.nbt = nbt;
    }

    public <V extends TransferVariant<T>> boolean insertInto(Storage<V> storage, BiFunction<T, NbtCompound, V> of, TransactionContext transaction)
    {
        update();
        if (!willOutput)
            return true;

        V variant = of.apply(resource, nbt);
        long inserted = storage.insert(variant, amount, transaction);
        return inserted == amount;
    }

    public static RecipeOutput<?> fromJson(JsonObject json)
    {
        throw new NotImplementedException();
    }

    public static <R> RecipeOutput<R> fromJsonRegistry(Registry<R> registry, JsonObject json)
    {
        R resource;
        if (json.has("resource"))
        {
            Identifier id = new Identifier(JsonHelper.getString(json, "resource"));
            resource = registry.getOrEmpty(id).orElseThrow(() -> new JsonSyntaxException("Unknown resource '" + id + "'"));
        }
        else throw new JsonSyntaxException("Output resource must be defined");

        float probability = 1;
        if (json.has("chance"))
        {
            probability = JsonHelper.getFloat(json, "chance");
        }

        if (json.has("amount"))
        {
            int amount = JsonHelper.getInt(json, "amount");
            return new RecipeOutput<R>(resource, amount, amount, probability);
        }
        else if (json.has("min") && json.has("max"))
        {
            int min = JsonHelper.getInt(json, "min");
            int max = JsonHelper.getInt(json, "max");
            return new RecipeOutput<>(resource, min, max, probability);
        }
        else throw new JsonSyntaxException("Either \"amount\", or \"min\" and \"max\" must be defined");
    }

    public static <R> RecipeOutput<R> fromBuffer(Registry<R> registry, PacketByteBuf buf)
    {
        Identifier id = buf.readIdentifier();
        R resource = registry.get(id);
        int min = buf.readVarInt();
        int max = buf.readVarInt();
        float chance = buf.readFloat();
        return new RecipeOutput<>(resource, min, max, chance);
    }

    public void write(Registry<T> registry, PacketByteBuf buf)
    {
        buf.writeIdentifier(registry.getId(resource));
        buf.writeVarInt(lootFunction.getMin());
        buf.writeVarInt(lootFunction.getMax());
        buf.writeFloat(chance);
    }
}
