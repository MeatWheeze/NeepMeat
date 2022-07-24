package com.neep.meatlib.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RecipeOutput<T>
{
    protected final T resource;
    protected final UniformIntProvider lootFunction;
    protected final Random random;
    protected int amount;

    public RecipeOutput(@NotNull T resource, int min, int max)
    {
        this.resource = resource;
        this.lootFunction = UniformIntProvider.create(min, max);
        this.random = new Random();
    }

    public T resource()
    {
        return resource;
    }

    public long amount()
    {
        return amount;
    }

    public void update()
    {
        amount = lootFunction.get(random);
    }

    public static <R> RecipeOutput<R> fromJson(Registry<R> registry, JsonObject json)
    {
        R resource;
        if (json.has("resource"))
        {
            Identifier id = new Identifier(JsonHelper.getString(json, "resource"));
            resource = registry.getOrEmpty(id).orElseThrow(() -> new JsonSyntaxException("Unknown resource '" + id + "'"));
        }
        else throw new JsonSyntaxException("Output resource must be defined");

        if (json.has("amount"))
        {
            int amount = JsonHelper.getInt(json, "amount");
            return new RecipeOutput<R>(resource, amount, amount);
        }
        else if (json.has("min") && json.has("max"))
        {
            int min = JsonHelper.getInt(json, "min");
            int max = JsonHelper.getInt(json, "max");
            return new RecipeOutput<>(resource, min, max);
        }
        else throw new JsonSyntaxException("Either \"amount\", or \"min\" and \"max\" must be defined");
    }

    public static <R> RecipeOutput<R> fromBuffer(Registry<R> registry, PacketByteBuf buf)
    {
        Identifier id = buf.readIdentifier();
        R resource = registry.get(id);
        int min = buf.readVarInt();
        int max = buf.readVarInt();
        return new RecipeOutput<>(resource, min, max);
    }

    public void write(Registry<T> registry, PacketByteBuf buf)
    {
        buf.writeIdentifier(registry.getId(resource));
        buf.writeVarInt(lootFunction.getMin());
        buf.writeVarInt(lootFunction.getMax());
    }
}
