package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;

public class TankMessagePacket
{
    public static final Identifier TANK_MESSAGE = new Identifier(NeepMeat.NAMESPACE, "tank_message");

    public static void send(ServerPlayerEntity player, BlockPos pos, long amount, FluidVariant resource)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(pos);
        buf.writeLong(amount);
        buf.writeNbt(resource.toNbt());

        ServerPlayNetworking.send(player, TANK_MESSAGE, buf);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(TANK_MESSAGE, (client, handler, buf, responseSender) ->
            {
                BlockPos pos = buf.readBlockPos();
                long amount = buf.readLong();
                FluidVariant resource = FluidVariant.fromNbt(buf.readNbt());
                MutableText text = FluidVariantAttributes.getName(resource).copy();

//                long buckets = amount / FluidConstants.BUCKET;
//                long remainder = amount % FluidConstants.BUCKET;
//                double dRemainder = remainder / FluidConstants.BUCKET;

                double buckets = (double) amount / FluidConstants.BUCKET;

                DecimalFormat df = new DecimalFormat("###.###");
                if (buckets > 10)
                {
                    client.player.sendMessage(resource.isBlank() ? Text.of("Empty") : text.append(": " +  df.format(buckets) + "b"), true);
                }
                else
                {
                    client.player.sendMessage(resource.isBlank() ? Text.of("Empty") : text.append(": " + MiscUtil.dropletsToMb(amount) + "mb"), true);
                }

            });
        }
    }
}
