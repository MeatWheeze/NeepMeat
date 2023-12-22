package com.neep.neepmeat.player.upgrade;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

public class PlayerUpgradeRegistry
{
    public static final SimpleRegistry<PlayerUpgradeConstructor> REGISTRY = FabricRegistryBuilder.createSimple(
            PlayerUpgradeConstructor.class, new Identifier(NeepMeat.NAMESPACE, "player_upgrade")).buildAndRegister();

    @FunctionalInterface
    public interface PlayerUpgradeConstructor
    {
        PlayerUpgrade create(PlayerEntity player);
    }
}
