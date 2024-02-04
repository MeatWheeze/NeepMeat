package com.neep.neepmeat.network.plc;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PLCRobotC2S
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "robot_update");

    public static void send(PLCBlockEntity be, ServerWorld world)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        PacketBufUtil.writeBlockPos(buf, be.getPos());
        buf.writeDouble(be.getSurgeryRobot().getX());
        buf.writeDouble(be.getSurgeryRobot().getY());
        buf.writeDouble(be.getSurgeryRobot().getZ());

        buf.writeFloat(be.getSurgeryRobot().getPitch());
        buf.writeFloat(be.getSurgeryRobot().getYaw());

        PlayerLookup.around(world, be.getPos(), 30).forEach(e ->
        {
            ServerPlayNetworking.send(e, ID, buf);
        });
    }

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, PLCRobotC2S::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        BlockPos pos = PacketBufUtil.readBlockPos(buf);
        World world = player.getWorld();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        float pitch = buf.readFloat();
        float yaw = buf.readFloat();

        server.execute(() ->
        {
            if (world.getBlockEntity(pos) instanceof PLCBlockEntity be)
            {
                be.getSurgeryRobot().setPos(x, y, z);
                be.getSurgeryRobot().setPitchYaw(pitch, yaw);
            }
        });
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void send(PLCBlockEntity be)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            PacketBufUtil.writeBlockPos(buf, be.getPos());
            buf.writeDouble(be.getSurgeryRobot().getX());
            buf.writeDouble(be.getSurgeryRobot().getY());
            buf.writeDouble(be.getSurgeryRobot().getZ());

            buf.writeFloat(be.getSurgeryRobot().getPitch());
            buf.writeFloat(be.getSurgeryRobot().getYaw());

            ClientPlayNetworking.send(ID, buf);
        }

        public static void init()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, Client::apply);
        }

        private static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
        {
            BlockPos pos = PacketBufUtil.readBlockPos(buf);
            World world = client.world;

            if (world == null)
                return;

            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            float pitch = buf.readFloat();
            float yaw = buf.readFloat();

            client.execute(() ->
            {
                if (world.getBlockEntity(pos) instanceof PLCBlockEntity be)
                {
                    be.getSurgeryRobot().setPos(x, y, z);
                    be.getSurgeryRobot().setPitchYaw(pitch, yaw);
                }
            });
        }
    }
}
