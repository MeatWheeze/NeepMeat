package com.neep.neepmeat.network;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PLCRobotEnterS2C
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "plc_robot_enter");

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
                        client.world.getBlockEntity(pos, NMBlockEntities.PLC).ifPresent(be ->
                        {
                            PLCHudRenderer.enter(be);
                        });
                    }
                });
            });
        }
    }
}
