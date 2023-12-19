package com.neep.neepmeat.implant.player;

import com.google.common.collect.Maps;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.network.PlayerImplantStatusS2CPacket;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class PlayerImplantManager implements ImplantManager, Component, ServerTickingComponent, AutoSyncedComponent, ClientTickingComponent
{
    public static final String ID = "neepmeat:upgrades";
    public static final String TRANSLATION_PREFIX = "implant";

    protected final PlayerEntity player;
    protected Map<Identifier, EntityImplant> implants = Maps.newHashMap();

    public PlayerImplantManager(PlayerEntity player)
    {
        this.player = player;
    }

    public static Text getImplantName(Identifier id)
    {
        return Text.translatable(id.toTranslationKey(TRANSLATION_PREFIX));
    }

    @Override
    public void installImplant(Identifier id)
    {
        if (ImplantRegistry.REGISTRY.containsId(id))
        {
            if (!player.getWorld().isClient())
            {
                player.sendMessage(Text.translatable("message." + NeepMeat.NAMESPACE + ".implant.install", getImplantName(id)), true);
            }

            addImplant(id);
        }
        else
        {
            throw new IllegalArgumentException("Tried to add an unregistered implant to player " + player.getEntityName());
        }
    }

    protected EntityImplant addImplant(Identifier id)
    {
        ImplantRegistry.Constructor constructor = ImplantRegistry.REGISTRY.get(id);;

        if (constructor == null) throw new IllegalArgumentException("Tried to add an unregistered implant to player " + player.getEntityName());

        EntityImplant implant = constructor.create(player);
        implants.put(id, implant);
        implant.onInstall();
        sync();
        return implant;
    }

    @Override
    public void removeImplant(Identifier id)
    {
        if (id == null)
            return;

        if (!player.getWorld().isClient())
        {
            PlayerImplantStatusS2CPacket.send((ServerPlayerEntity) player, id, PlayerImplantStatusS2CPacket.Status.REMOVE);
        }

        EntityImplant upgrade = implants.get(id);
        if (upgrade != null)
        {
            upgrade.onUninstall();
            implants.remove(id);
            sync();
        }
    }

    @Override
    public Set<Identifier> getInstalled()
    {
        return implants.keySet();
    }

    public float getProtectionAmount(DamageSource source, float amount)
    {
        return (float) implants.values().stream().mapToDouble(u -> u.getProtectionAmount(source, amount)).sum();
    }

    public static PlayerImplantManager get(PlayerEntity player)
    {
        return (PlayerImplantManager) player.getComponent(NMComponents.IMPLANT_MANAGER);
    }

    @Nullable
    public EntityImplant getImplant(Identifier id)
    {
        return implants.get(id);
    }

    @Override
    public void serverTick()
    {
        implants.values().forEach(EntityImplant::tick);
    }

    @Override
    public void clientTick()
    {
        implants.values().forEach(EntityImplant::clientTick);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbt)
    {
        NbtList list = nbt.getList("upgrades", NbtElement.COMPOUND_TYPE);
        list.forEach(nbt1 ->
        {
            NbtCompound implantNbt = (NbtCompound) nbt1;
            Identifier id = Identifier.tryParse(implantNbt.getString("id"));
            EntityImplant implant = addImplant(id);
            implant.readNbt(implantNbt);
        });
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbt)
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
    }

    private void sync()
    {
        NMComponents.IMPLANT_MANAGER.sync(player);
    }

    public static void init()
    {
        ServerPlayConnectionEvents.INIT.register((handler, server) ->
        {
            get(handler.getPlayer()).implants.values().forEach(EntityImplant::onPlayerInit);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
        {
            PlayerImplantManager manager = get(handler.getPlayer());
            manager.implants.values().forEach(EntityImplant::onPlayerRemove);
        });

    }
}
