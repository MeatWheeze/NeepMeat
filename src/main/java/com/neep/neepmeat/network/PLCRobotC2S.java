package com.neep.neepmeat.network;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.vat.ItemPortBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PLCRobotC2S
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "robot_update");

    public static void send(PLCBlockEntity be)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        PacketBufUtil.writeBlockPos(buf, be.getPos());
        buf.writeDouble(be.getRobot().getX());
        buf.writeDouble(be.getRobot().getY());
        buf.writeDouble(be.getRobot().getZ());

        ClientPlayNetworking.send(ID, buf);
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

        server.execute(() ->
        {
            if (world.getBlockEntity(pos) instanceof PLCBlockEntity be)
            {
                be.getRobot().setPos(x, y, z);
            }
        });
//        world.getBlockEntity(pos, NMBlockEntities.PLC).ifPresent(be ->
//        {
//            be.getRobot().readBuf(buf);
//        });
    }
}
