package com.neep.neepmeat.network.plc;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.program.PlcProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PLCSyncProgram
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "plc_sync_program");

    public static void send()
    {

    }

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, PLCSyncProgram::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        BlockPos pos = PacketBufUtil.readBlockPos(buf);
        NbtCompound nbt = buf.readNbt();

        server.execute(() ->
        {
            if (player.world.getBlockEntity(pos) instanceof PLCBlockEntity be)
            {
                if (be.getEditProgram() != null)
                {
                    be.getEditProgram().readNbt(nbt);
                }
            }
        });
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void syncProgram(PLCBlockEntity be, PlcProgram program)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            PacketBufUtil.writeBlockPos(buf, be.getPos());
            buf.writeNbt(program.writeNbt(new NbtCompound()));
        }
    }
}
