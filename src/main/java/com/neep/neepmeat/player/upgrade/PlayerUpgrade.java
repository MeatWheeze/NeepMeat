package com.neep.neepmeat.player.upgrade;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerUpgrade extends NbtSerialisable
{
    void tick();

    default void onPlayerInit() {}

    default void onPlayerRemove() {}

    default void onRespawn(PlayerEntity oldPlayer, PlayerEntity newPlayer) {}

    default void onInstall() {}

    default void onUninstall() {}
}
