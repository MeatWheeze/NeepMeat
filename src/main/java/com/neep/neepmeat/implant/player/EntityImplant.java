package com.neep.neepmeat.implant.player;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.entity.damage.DamageSource;

public interface EntityImplant extends NbtSerialisable
{
    void tick();

    default void clientTick() {}

    default void onPlayerInit() {}

    default void onPlayerRemove() {}

    default void onInstall() {}

    default void onUninstall() {}

    default float getProtectionAmount(DamageSource source, float amount) { return 0; }
}
