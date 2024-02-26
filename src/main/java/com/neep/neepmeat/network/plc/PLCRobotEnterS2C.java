package com.neep.neepmeat.network.plc;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.network.S2CSender;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PLCRobotEnterS2C
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "plc_robot_enter");

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) ->
        {
            BlockPos pos = PacketBufUtil.readBlockPos(buf);

            server.execute(() ->
            {
                if (player.world.getBlockEntity(pos) instanceof PLCBlockEntity be)
                {
                    be.exit();
                }
            });
        });
    }

    public static void send(PlayerEntity player, PLCBlockEntity be)
    {
        var buf = S2CSender.getBuf();

        PacketBufUtil.writeBlockPos(buf, be.getPos());

        S2CSender.send(player, ID, buf);
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) ->
            {
                BlockPos pos = PacketBufUtil.readBlockPos(buf);
                client.execute(() ->
                {
                    if (client.world != null)
                    {
                        client.world.getBlockEntity(pos, PLCBlocks.PLC_ENTITY).ifPresent(be ->
                        {
                            PLCHudRenderer.enter(be);
                        });
                    }
                });
            });
        }

        public static void send(PLCBlockEntity be)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            PacketBufUtil.writeBlockPos(buf, be.getPos());

            ClientPlayNetworking.send(ID, buf);
        }
    }
}
