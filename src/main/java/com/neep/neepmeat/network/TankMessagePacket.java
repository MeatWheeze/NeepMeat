package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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
    public static void registerReciever()
    {
        ClientPlayNetworking.registerGlobalReceiver(TANK_MESSAGE, (client, handler, buf, responseSender) ->
        {
            BlockPos pos = buf.readBlockPos();
            long amount = buf.readLong();
            FluidVariant resource = FluidVariant.fromNbt(buf.readNbt());
            MutableText text = FluidVariantRendering.getName(resource).shallowCopy();

            long mb = Math.floorDiv(amount, FluidConstants.BUCKET / 1000);
            client.player.sendMessage(resource.isBlank() ? Text.of("Empty") : text.append(": " + mb + "mb"), true);
        });
    }
}
