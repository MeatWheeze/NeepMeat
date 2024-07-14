package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.joml.Matrix4f;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public abstract class AbstractMeatgunModule implements MeatgunModule
{
    protected Matrix4f transform = new Matrix4f();
    protected List<ModuleSlot> slots;
    protected UUID uuid = UUID.randomUUID();
    protected final MeatgunComponent.Listener listener;

    public AbstractMeatgunModule(MeatgunComponent.Listener listener, List<ModuleSlot> slots)
    {
        this.listener = listener;
        this.slots = Collections.unmodifiableList(slots);
    }

    public AbstractMeatgunModule(MeatgunComponent.Listener listener)
    {
        this.listener = listener;
        this.slots = Collections.emptyList();
    }

    protected void setSlots(List<ModuleSlot> slots)
    {
        this.slots = Collections.unmodifiableList(slots);
    }

    @Override
    public UUID getUuid()
    {
        return uuid;
    }

    @Override
    public void setTransform(Matrix4f transform)
    {
        this.transform = transform;
    }

    @Override
    public List<ModuleSlot> getChildren()
    {
        return slots;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putUuid("uuid", uuid);

        float[] fs = transform.get(new float[16]);
        nbt.putIntArray("transform", IntStream.range(0, fs.length).map(i -> Float.floatToIntBits(fs[i])).toArray());

        NbtList slotsNbt = new NbtList();
        for (var slot : slots)
        {
            NbtCompound slotNbt = MeatgunModule.toNbt(slot.get());
            slotsNbt.add(slotNbt);
        }
        nbt.put("slots", slotsNbt);


        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        if (nbt.containsUuid("uuid"))
            this.uuid = nbt.getUuid("uuid");

        int[] bits = nbt.getIntArray("transform");
        if (bits.length == 16) // Prevent crash if tag is empty.
        {
            float[] fs = new float[bits.length];
            for (int i = 0; i < bits.length; ++i)
            {
                fs[i] = Float.intBitsToFloat(bits[i]);
            }
            transform.set(fs);
        }
        else
        {
            transform = new Matrix4f();
        }

        NbtList list = nbt.getList("slots", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < slots.size(); ++i)
        {
            Type<?> preType = slots.get(i).get().getType();
            NbtCompound slotNbt = list.getCompound(i);
            Type<?> type = MeatgunModule.readType(slotNbt);
            if (type != preType)
            {
                MeatgunModule module = type.create(listener, slotNbt);
                module.readNbt(slotNbt);
                slots.get(i).set(module);
            }
            else
            {
                slots.get(i).get().readNbt(slotNbt);
            }
        }
    }

//    interface ModuleCodec<T extends MeatgunModule>
//    {
//        T decode(NbtCompound nbt);
//
//        void encode(NbtCompound nbt);
//    }

//    interface Updater<A>
//    {
//        <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final T input);
//    }
//
//    interface ModuleCodec<A> extends Codec<A>
//    {
//        <T> DataResult<Pair<A, T>> update(final DynamicOps<T> ops, final T input);
//
//        static <A> ModuleCodec<A> of(Encoder<A> encoder, Decoder<A> decoder, Updater<A> updater)
//        {
//            return new ModuleCodec<>()
//            {
//                @Override
//                public <T> DataResult<Pair<A, T>> update(DynamicOps<T> ops, T input)
//                {
//                    return updater.decode(ops, input);
//                }
//
//                @Override
//                public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final T input)
//                {
//                    return decoder.decode(ops, input);
//                }
//
//                @Override
//                public <T> DataResult<T> encode(final A input, final DynamicOps<T> ops, final T prefix)
//                {
//                    return encoder.encode(input, ops, prefix);
//                }
//
//                @Override
//                public String toString()
//                {
//                    return "Codec[" + encoder + " " + updater + " " + decoder + "]";
//                }
//            };
//        }
//    }
}
