package com.neep.neepmeat.implant.player;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;

public interface EntityImplant extends NbtSerialisable
{
    void tick();

    default void clientTick() {}

    default void onPlayerInit() {}

    default void onPlayerRemove() {}

    default void onInstall() {}

    default void onUninstall() {}

    default float getProtectionAmount(DamageSource source, float amount) { return 0; }

    EntityImplant DEFAULT = new EntityImplant()
    {
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
    };
}
