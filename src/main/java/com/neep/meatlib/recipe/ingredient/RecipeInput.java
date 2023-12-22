package com.neep.meatlib.recipe.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Represents a required quantity of an ingredient, supplied either by a tag or a specific resource.
 * @param <T> A resource with a corresponding registry, such as Item or Fluid
 */
@SuppressWarnings("UnstableApiUsage")
public class RecipeInput<T> implements Predicate<StorageView<? extends TransferVariant<T>>>
{
    protected final Serialiser<T> serialiser;
    protected final Identifier type;

    // Only one entry at the moment.
    protected Entry<T> entry;

    protected long amount;
    @Nullable protected T[] matchingStacks;

    protected RecipeInput(Entry<T> entry, long amount, Serialiser<T> serialiser, Identifier type)
    {
        this.entry = entry;
        this.amount = amount;
        this.serialiser = serialiser;
        this.type = type;
    }

    public Serialiser<T> getSerialiser()
    {
        return serialiser;
    }

    public Identifier getType()
    {
        return type;
    }

    /**
     * @param json Recipe JSON object
     * @return A RecipeInput based on the detected resource type
     */
    public static RecipeInput<?> fromJson(JsonObject json)
    {
        Identifier type;
        Serialiser<?> serialiser;
        if (json.has("type"))
        {
            type = new Identifier(JsonHelper.getString(json, "type"));
            serialiser = RecipeInputs.SERIALISERS.get(type);
        }
        else throw new JsonParseException("Recipe does not specify resource type.");

        if (serialiser == null) throw new JsonParseException("Unknown resource type '" + type +"'");

        return serialiser.fromJson(json);
    }

    public static <R> RecipeInput<R> fromBuffer(PacketByteBuf buf)
    {
        Identifier type = new Identifier(buf.readString());
        Serialiser<?> serialiser = RecipeInputs.SERIALISERS.get(type);
        if (serialiser == null) throw new IllegalStateException("Error reading meatlib recipe packet: invalid type");

        return (RecipeInput<R>) serialiser.fromBuffer(buf);
    }

    public static <R> RecipeInput<R> fromJsonRegistry(Serialiser<R> serialiser, JsonObject json)
    {
        return serialiser.fromJson(json);
    }

    public void write(PacketByteBuf buf)
    {
        serialiser.write(buf, this);
    }

    public long amount()
    {
        return amount;
    }

    public boolean isEmpty()
    {
        return this.amount == 0 || this == RecipeInputs.EMPTY;
    }

    public void cacheMatching()
    {
        if (this.matchingStacks == null)
        {
            this.matchingStacks = (T[]) (entry.getMatching().stream()).distinct().toArray();
        }
    }

    @Override
    public boolean test(StorageView<? extends TransferVariant<T>> storageView)
    {
        cacheMatching();
        return Arrays.stream(matchingStacks).anyMatch(o -> storageView.getResource().getObject().equals(o));
    }

    public boolean test(Storage<? extends TransferVariant<?>> storage, TransactionContext transaction)
    {
        cacheMatching();
        Stream<?> entries = Arrays.stream(matchingStacks);
        for (StorageView<? extends TransferVariant<?>> view : storage.iterable(transaction))
        {
            if (entries.anyMatch(o -> view.getResource().getObject().equals(o))) return true;
        }
        return false;
    }

    public Optional<T> getFirstMatching(StorageView<? extends TransferVariant<T>> view)
    {
        cacheMatching();
        return Arrays.stream(matchingStacks).filter(t -> view.getResource().getObject().equals(t)).findFirst();
    }

    public Optional<T> getFirstMatching(Storage<? extends TransferVariant<?>> storage, TransactionContext transaction)
    {
        cacheMatching();
        for (StorageView<? extends TransferVariant<?>> view : storage.iterable(transaction))
        {
            Optional<T> optional = Arrays.stream(matchingStacks).filter(t -> view.getResource().getObject().equals(t)).findFirst();
            if (optional.isPresent())
                return optional;
        }
        return Optional.empty();
    }

    public <V extends TransferVariant<T>> Optional<V> getFirstMatching(Storage<V> storage, NbtCompound nbt, BiFunction<T, NbtCompound, V> func, TransactionContext transaction)
    {
        cacheMatching();
        for (StorageView<V> view : storage.iterable(transaction))
        {
            Optional<T> optional = Arrays.stream(matchingStacks).filter(t ->
            {
                V variant = view.getResource();
                return variant.getObject().equals(t) && variant.nbtMatches(nbt);
            }).findFirst();

            if (optional.isPresent())
            {
                return Optional.of(func.apply(optional.get(), nbt));
            }
        }
        return Optional.empty();
    }

    public Collection<T> getAll()
    {
        return entry.getMatching();
    }

    public interface Entry<T>
    {
        Entry<Object> EMPTY = () -> Collections.EMPTY_SET;

        Collection<T> getMatching();
    }

    public static class TagEntry<T> implements Entry<T>
    {
        protected TagKey<T> tag;

        TagEntry(TagKey<T> tag)
        {
            this.tag = tag;
        }

        @Override
        public Collection<T> getMatching()
        {
            ArrayList<T> list = new ArrayList<>();
            Registry<T> registry = getRegistry(tag.registry());
            for (RegistryEntry<T> registryEntry : registry.iterateEntries(this.tag))
            {
                list.add(registryEntry.value());
            }
            return list;
        }

        // TODO: Find a way of getting registry from RegistryKey
        @SuppressWarnings("unchecked")
        public static <T> Registry<T> getRegistry(RegistryKey<? extends Registry<T>> key)
        {
            if (Registry.ITEM_KEY.equals(key))
            {
                return (Registry<T>) Registry.ITEM;
            }
            else if (Registry.FLUID_KEY.equals(key))
            {
                return (Registry<T>) Registry.FLUID;
            }
            throw new NotImplementedException();
        }
    }

    public static class ResourceEntry<T> implements Entry<T>
    {
        protected T object;

        public ResourceEntry(T resource)
        {
            this.object = resource;
        }

        public T getObject()
        {
            return object;
        }

        @Override
        public Collection<T> getMatching()
        {
            return List.of(object);
        }
    }

    public interface Serialiser<T>
    {
        RecipeInput<T> fromJson(JsonObject json);
        RecipeInput<T> fromBuffer(PacketByteBuf buf);

        void write(PacketByteBuf buf, RecipeInput<T> input);
        T getObject(Identifier id);
        Identifier getId(T t);
    }

    public static class RegistrySerialiser<R> implements Serialiser<R>
    {
        private final Registry<R> registry;

        public RegistrySerialiser(Registry<R> registry)
        {
            this.registry = registry;
        }

        @Override
        public RecipeInput<R> fromJson(JsonObject json)
        {
            Entry<R> entry = null;
            if (json.has("resource"))
            {
                Identifier id = new Identifier(JsonHelper.getString(json, "resource"));
                R resource = registry.get(id);
                if (resource == null) throw new JsonSyntaxException("Unknown resource '" + id + "'");
                entry = new ResourceEntry<>(resource);
            }
            if (json.has("tag"))
            {
                Identifier identifier = new Identifier(JsonHelper.getString(json, "tag"));
                TagKey<R> tagKey = TagKey.of(registry.getKey(), identifier);
                entry =  new TagEntry<>(tagKey);
            }

            if (entry == null)
            {
                throw new JsonSyntaxException("No resource or tag specified.");
            }
            long amount = JsonHelper.getLong(json, "amount");

            return new RecipeInput<>(entry, amount, this, registry.getKey().getValue());
        }

        public RecipeInput<R> fromBuffer(PacketByteBuf buf)
        {
            Entry<R> entry = null;
            boolean isTag = buf.readBoolean();
            if (isTag)
            {
                Identifier id = buf.readIdentifier();
                TagKey<R> tagKey = TagKey.of(registry.getKey(), id);
                entry = new TagEntry<>(tagKey);
            }
            else
            {
                Identifier id = buf.readIdentifier();
                R r = registry.get(id);
                entry = new ResourceEntry<>(r);
            }
            long amount = buf.readVarLong();

            return new RecipeInput<>(entry, amount, this, registry.getKey().getValue());
        }

        @Override
        public void write(PacketByteBuf buf, RecipeInput<R> input)
        {
            // Write recipe resource type
            buf.writeString(input.getType().toString());

            // This jank is here to avoid making another registry.
            boolean isTag = input.entry instanceof TagEntry;
            buf.writeBoolean(isTag);
            if (isTag)
            {
                buf.writeIdentifier(((TagEntry<R>) input.entry).tag.id());
            }
            else
            {
                buf.writeIdentifier(getId(((ResourceEntry<R>) input.entry).getObject()));
            }
            buf.writeVarLong(input.amount());
        }

        @Override
        public R getObject(Identifier id)
        {
            return registry.get(id);
        }

        @Override
        public Identifier getId(R t)
        {
            return registry.getId(t);
        }
    }

}