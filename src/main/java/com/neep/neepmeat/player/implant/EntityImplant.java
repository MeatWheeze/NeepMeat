package com.neep.neepmeat.player.implant;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public interface EntityImplant extends NbtSerialisable
{
    void tick();

    default void onPlayerInit() {}

    default void onPlayerRemove() {}

    default void onRespawn(PlayerEntity oldPlayer, PlayerEntity newPlayer) {}

    default void onInstall() {}

    default void onUninstall() {}

    default float getProtectionAmount(DamageSource source, float amount) { return 0; }
}
