package com.neep.neepmeat.player.upgrade;

import com.google.common.collect.Maps;
import com.neep.meatlib.api.event.EntityNbtEvents;
import com.neep.meatlib.api.event.InitialTicks;
import com.neep.meatlib.attachment.player.PlayerAttachment;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.network.PlayerUpgradeStatusS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.Map;

public class PlayerUpgradeManager implements PlayerAttachment, NbtSerialisable
{
    public static final String ID = "neepmeat:upgrades";
    protected PlayerEntity player;
    protected Map<Identifier, PlayerUpgrade> upgrades = Maps.newHashMap();

    protected static final String KEY_ROOT = "neepmeat:upgrades";

    public PlayerUpgradeManager(PlayerEntity player)
    {
        this.player = player;
    }

    public void installUpgrade(Identifier id)
    {

        if (PlayerUpgradeRegistry.REGISTRY.containsId(id))
        {
            if (!player.getWorld().isClient())
            {
                PlayerUpgradeStatusS2CPacket.send((ServerPlayerEntity) player, id, PlayerUpgradeStatusS2CPacket.Status.INSTALL);
            }

            addUpgrade(id);
        }
        else
        {
            throw new IllegalArgumentException("Tried to add an unregistered player upgrade");
        }
    }

    protected void addUpgrade(Identifier id)
    {
        PlayerUpgradeRegistry.PlayerUpgradeConstructor constructor = PlayerUpgradeRegistry.REGISTRY.get(id);;

        if (constructor == null) throw new IllegalArgumentException("Tried to add an unregistered player upgrade");

        PlayerUpgrade upgrade = constructor.create(player);
        upgrades.put(id, constructor.create(player));
        upgrade.onInstall();
    }

    public void sync(NbtCompound fullNbt)
    {
        readNbt(fullNbt);
//        PlayerUpgradeRegistry.PlayerUpgradeConstructor constructor = PlayerUpgradeRegistry.REGISTRY.get(id);
//        if (constructor != null)
//        {
//            PlayerUpgrade upgrade = constructor.create(player);
//            upgrades.put(id, constructor.create(player));
//            upgrade.readNbt(initialNbt);
//            upgrade.onInstall();
//        }
//        else
//        {
//            throw new IllegalArgumentException("Tried to add an unregistered player upgrade");
//        }
    }

    public void removeUpgrade(Identifier id)
    {
        PlayerUpgradeRegistry.PlayerUpgradeConstructor constructor = PlayerUpgradeRegistry.REGISTRY.get(id);
        if (constructor != null)
        {
            if (!player.getWorld().isClient())
            {
                PlayerUpgradeStatusS2CPacket.send((ServerPlayerEntity) player, id, PlayerUpgradeStatusS2CPacket.Status.REMOVE);
            }

            PlayerUpgrade upgrade = upgrades.get(id);
            upgrade.onUninstall();
            upgrades.remove(id);
        }
        else
        {
            throw new IllegalArgumentException("Tried to remove an unregistered player upgrade");
        }
    }

    @Override
    public void tickAttachment()
    {
        upgrades.values().forEach(PlayerUpgrade::tick);
    }

    public float getProtectionAmount(DamageSource source, float amount)
    {
        return (float) upgrades.values().stream().mapToDouble(u -> u.getProtectionAmount(source, amount)).sum();
    }

    protected void addAll(PlayerUpgradeManager manager)
    {
        upgrades.putAll(manager.upgrades);
    }

    protected void deferredLoad(NbtCompound fullNbt)
    {
        if (fullNbt != null && player instanceof ServerPlayerEntity serverPlayer)
        {
            PlayerUpgradeStatusS2CPacket.sendLoad(serverPlayer, fullNbt);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        upgrades.forEach((i, u) ->
        {
            NbtCompound nbt1 = new NbtCompound();
            nbt1.putString("id", i.toString());
            u.writeNbt(nbt1);
            list.add(nbt1);
        });
        nbt.put("upgrades", list);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        NbtList list = nbt.getList("upgrades", NbtType.COMPOUND);
        list.forEach(nbt1 ->
        {
            NbtCompound upgradeNbt = (NbtCompound) nbt1;
            Identifier id = Identifier.tryParse(upgradeNbt.getString("id"));
            addUpgrade(id);
        });
    }

    public static PlayerUpgradeManager get(PlayerEntity player)
    {
        return player.meatlib$getAttachmentManager().getAttachment(ID);
    }

    public static void init()
    {
        ServerPlayConnectionEvents.INIT.register((handler, server) ->
        {
            get(handler.getPlayer()).upgrades.values().forEach(PlayerUpgrade::onPlayerInit);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
        {
            PlayerUpgradeManager oldManager = get(oldPlayer);
            PlayerUpgradeManager newManager = get(newPlayer);

            // Sync old upgrades to new manager
            newManager.addAll(oldManager);
            newManager.upgrades.values().forEach(u -> u.onRespawn(oldPlayer, newPlayer));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
        {
            PlayerUpgradeManager manager = get(handler.getPlayer());
            manager.upgrades.values().forEach(PlayerUpgrade::onPlayerRemove);
        });


        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) ->
        {
            // Sync newly loaded data to the client player.
//            PlayerUpgradeStatusS2CPacket.sendLoad(handler.getPlayer(), );
        }));

        EntityNbtEvents.WRITE.register((entity, nbt) ->
        {
            if (entity instanceof PlayerEntity player1)
            {
                NbtCompound nbtCompound = new NbtCompound();
                get(player1).writeNbt(nbtCompound);

                // Insert upgrade manager NBT
                nbt.put(KEY_ROOT, nbtCompound);
            }
        });

        EntityNbtEvents.READ.register((entity, nbt) ->
        {
            if (entity instanceof PlayerEntity player1 && player1.getWorld() instanceof ServerWorld serverWorld)
            {
                NbtCompound nbtCompound = nbt.getCompound(KEY_ROOT);

                // Unsure whether this state is possible
                if (nbtCompound == null) return;

                PlayerUpgradeManager manager = get(player1);
                manager.readNbt(nbtCompound);

                InitialTicks.getInstance(serverWorld).queue(w -> manager.deferredLoad(nbtCompound));
            }
        });
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
            {
                if (client.player != null)
                    get(client.player).upgrades.values().forEach(PlayerUpgrade::onPlayerRemove);
            });

            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
            {
                if (client.player != null)
                    get(client.player).upgrades.values().forEach(PlayerUpgrade::onPlayerInit);
            });
        }
    }
}
