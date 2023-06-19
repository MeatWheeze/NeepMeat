package com.neep.neepmeat.entity.worm;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public interface WormAction extends NbtSerialisable
{
    default void init() {}

    Identifier getId();

    void tick();
    boolean isFinished();

    class EmptyAction implements WormAction
    {
        public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "empty");

        public EmptyAction(WormEntity entity)
        {

        }

        @Override
        public Identifier getId()
        {
            return ID;
        }

        @Override
        public void tick()
        {

        }

        @Override
        public boolean isFinished()
        {
            return true;
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
