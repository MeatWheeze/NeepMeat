package com.neep.neepmeat.player.upgrade;

import com.google.common.collect.Maps;
import com.neep.meatlib.attachment.player.PlayerAttachment;
import com.neep.meatlib.util.NbtSerialisable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;

public class PlayerUpgradeManager implements PlayerAttachment, NbtSerialisable
{
    public static final String ID = "neepmeat:upgrades";
    protected PlayerEntity player;
    protected Map<Identifier, PlayerUpgrade> UPGRADES = Maps.newHashMap();

    public PlayerUpgradeManager(PlayerEntity player)
    {
        this.player = player;
//        put(SkeltalUpgrade.ID);
    }

    public void put(Identifier id)
    {
        PlayerUpgradeRegistry.PlayerUpgradeConstructor constructor = PlayerUpgradeRegistry.REGISTRY.get(id);
        if (constructor != null)
        {
            UPGRADES.put(id, constructor.create(player));
        }
        else
        {
            throw new IllegalArgumentException("Invalid or unregistered player upgrade");
        }
    }

    @Override
    public void tickAttachment()
    {
        UPGRADES.values().forEach(PlayerUpgrade::tick);
    }

    public float getProtectionAmount(DamageSource source, float amount)
    {
        return (float) UPGRADES.values().stream().mapToDouble(u -> u.getProtectionAmount(source, amount)).sum();
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

    public static PlayerUpgradeManager get(PlayerEntity player)
    {
        return player.meatlib$getAttachmentManager().getAttachment(ID);
    }

    static
    {
        ServerPlayConnectionEvents.INIT.register((handler, server) ->
        {
            get(handler.getPlayer()).UPGRADES.values().forEach(PlayerUpgrade::onPlayerInit);
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
        {
            if (client.player != null)
                get(client.player).UPGRADES.values().forEach(PlayerUpgrade::onPlayerInit);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
        {
            get(newPlayer).UPGRADES.values().forEach(u -> u.onRespawn(oldPlayer, newPlayer));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
        {
            get(handler.getPlayer()).UPGRADES.values().forEach(PlayerUpgrade::onPlayerRemove);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
        {
            if (client.player != null)
                get(client.player).UPGRADES.values().forEach(PlayerUpgrade::onPlayerRemove);
        });
    }

}
