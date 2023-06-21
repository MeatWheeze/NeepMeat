package com.neep.neepmeat.entity.worm;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public interface WormAction extends NbtSerialisable
{
    default void init() {}

    void tick();

    @Override
    default NbtCompound writeNbt(NbtCompound nbt) {return nbt;}

    @Override
    default void readNbt(NbtCompound nbt) {};

    @FunctionalInterface
    interface Sequence<T extends WormAction>
    {
        void tick(T parent, int counter);
    }

    class EmptyAction implements WormAction
    {
        public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "empty");

        public EmptyAction(WormEntity entity)
        {

        }

        @Override
        public void tick()
        {

        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt)
        {
            return nbt;
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {

        }
    }
}
