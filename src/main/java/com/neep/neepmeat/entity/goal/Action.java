package com.neep.neepmeat.entity.goal;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public interface Action<E> extends NbtSerialisable
{
    default void init() {}

    void tick();

    @Override
    default NbtCompound writeNbt(NbtCompound nbt) {return nbt;}

    @Override
    default void readNbt(NbtCompound nbt) {};

    @FunctionalInterface
    interface Sequence<T>
    {
        void tick(T parent, int counter);
    }

    static <E> Action<E> empty(E e)
    {
        return new EmptyAction<>(e);
    }

    class EmptyAction<E> implements Action<E>
    {
        public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "empty");

        public EmptyAction(E entity)
        {

        }

        @Override
        public void tick() { }
    }
}
