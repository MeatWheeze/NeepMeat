package com.neep.neepmeat.player.implant;

import com.google.common.collect.Maps;
import com.neep.meatlib.api.event.EntityNbtEvents;
import com.neep.meatlib.api.event.InitialTicks;
import com.neep.meatlib.attachment.player.PlayerAttachment;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.network.PlayerImplantStatusS2CPacket;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PlayerImplantManager implements PlayerAttachment, NbtSerialisable
{
    public static final String ID = "neepmeat:upgrades";
    protected PlayerEntity player;
    protected Map<Identifier, PlayerImplant> implants = Maps.newHashMap();

    protected static final String KEY_ROOT = "neepmeat:upgrades";

    public static final String TRANSLATION_PREFIX = "implant";

    public PlayerImplantManager(PlayerEntity player)
    {
        this.player = player;
    }

    public static Text getImplantName(Identifier id)
    {
        return Text.translatable(id.toTranslationKey(TRANSLATION_PREFIX));
    }

    public void installImplant(Identifier id)
    {

        if (PlayerImplantRegistry.REGISTRY.containsId(id))
        {
            if (!player.getWorld().isClient())
            {
                PlayerImplantStatusS2CPacket.send((ServerPlayerEntity) player, id, PlayerImplantStatusS2CPacket.Status.INSTALL);
                player.sendMessage(Text.translatable("message." + NeepMeat.NAMESPACE + ".implant.install", getImplantName(id)), true);
            }

            addImplant(id);
        }
        else
        {
            throw new IllegalArgumentException("Tried to add an unregistered implant to player " + player.getEntityName());
        }
    }

    protected void addImplant(Identifier id)
    {
        PlayerImplantRegistry.PlayerUpgradeConstructor constructor = PlayerImplantRegistry.REGISTRY.get(id);;

        if (constructor == null) throw new IllegalArgumentException("Tried to add an unregistered implant to player " + player.getEntityName());

        PlayerImplant upgrade = constructor.create(player);
        implants.put(id, constructor.create(player));
        upgrade.onInstall();
    }

    public void sync(NbtCompound fullNbt)
    {
        readNbt(fullNbt);
    }

    public void removeImplant(Identifier id)
    {
        PlayerImplantRegistry.PlayerUpgradeConstructor constructor = PlayerImplantRegistry.REGISTRY.get(id);
        if (constructor != null)
        {
            if (!player.getWorld().isClient())
            {
                PlayerImplantStatusS2CPacket.send((ServerPlayerEntity) player, id, PlayerImplantStatusS2CPacket.Status.REMOVE);
            }

            PlayerImplant upgrade = implants.get(id);
            upgrade.onUninstall();
            implants.remove(id);
        }
        else
        {
            throw new IllegalArgumentException("Tried to remove an unregistered implant in player " + player.getEntityName());
        }
    }

    @Override
    public void tickAttachment()
    {
        implants.values().forEach(PlayerImplant::tick);
    }

    public float getProtectionAmount(DamageSource source, float amount)
    {
        return (float) implants.values().stream().mapToDouble(u -> u.getProtectionAmount(source, amount)).sum();
    }

    protected void addAll(PlayerImplantManager manager)
    {
        implants.putAll(manager.implants);
    }

    protected void deferredLoad(NbtCompound fullNbt)
    {
        if (fullNbt != null && player instanceof ServerPlayerEntity serverPlayer)
        {
            PlayerImplantStatusS2CPacket.sendLoad(serverPlayer, fullNbt);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        implants.forEach((i, u) ->
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
            addImplant(id);
        });
    }

    public static PlayerImplantManager get(PlayerEntity player)
    {
        return player.meatlib$getAttachmentManager().getAttachment(ID);
    }

    public static void init()
    {
        ServerPlayConnectionEvents.INIT.register((handler, server) ->
        {
            get(handler.getPlayer()).implants.values().forEach(PlayerImplant::onPlayerInit);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
        {
            PlayerImplantManager oldManager = get(oldPlayer);
            PlayerImplantManager newManager = get(newPlayer);

            // Sync old upgrades to new manager
            newManager.addAll(oldManager);
            newManager.implants.values().forEach(u -> u.onRespawn(oldPlayer, newPlayer));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
        {
            PlayerImplantManager manager = get(handler.getPlayer());
            manager.implants.values().forEach(PlayerImplant::onPlayerRemove);
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

                PlayerImplantManager manager = get(player1);
                manager.readNbt(nbtCompound);

                InitialTicks.getInstance(serverWorld).queue(w -> manager.deferredLoad(nbtCompound));
            }
        });
    }

    @Nullable
    public PlayerImplant getImplant(Identifier id)
    {
        return implants.get(id);
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
            {
                if (client.player != null)
                    get(client.player).implants.values().forEach(PlayerImplant::onPlayerRemove);
            });

            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
            {
                if (client.player != null)
                    get(client.player).implants.values().forEach(PlayerImplant::onPlayerInit);
            });
        }
    }
}
