package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.player.implant.PlayerImplantManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerImplantStatusS2CPacket
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "player_upgrade_install");

    public static void sendLoad(ServerPlayerEntity player, NbtCompound initialNbt)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(Status.LOAD.ordinal());
        buf.writeNbt(initialNbt);

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void send(ServerPlayerEntity player, Identifier upgradeId, Status status)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(status.ordinal());
        buf.writeIdentifier(upgradeId);

        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
//            ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) ->
//            {
//                if (client.player == null) return;
//
//                Status status = Status.values()[buf.readVarInt()];
//                switch (status)
//                {
//                    case INSTALL -> PlayerImplantManager.get(client.player).installImplant(buf.readIdentifier());
//                    case REMOVE -> PlayerImplantManager.get(client.player).removeImplant(buf.readIdentifier());
//                    case LOAD ->
//                    {
//                        NbtCompound fullNbt = buf.readNbt();
//                        PlayerImplantManager.get(client.player).sync(fullNbt);
//                    }
//                }
//            });
        }
    }

    public enum Status
    {
        INSTALL,
        REMOVE,
        LOAD;
    }
}
