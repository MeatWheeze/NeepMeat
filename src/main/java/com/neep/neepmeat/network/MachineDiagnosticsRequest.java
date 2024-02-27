package com.neep.neepmeat.network;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.client.screen.MachineHudOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
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
import org.jetbrains.annotations.NotNull;

public class MachineDiagnosticsRequest
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "machine_diagnostics");

    public static void registerReceiver()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, MachineDiagnosticsRequest::applyServer);
    }

    private static void applyServer(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        Identifier worldId = Identifier.tryParse(buf.readString());
        ServerWorld world = (ServerWorld) handler.player.getWorld();

        // Not sure how this might happen
        if (!world.getRegistryKey().getValue().equals(worldId))
            return;

//        ServerWorld world = server.getWorld(RegistryKey.of(RegistryKeys.DIME, worldId));
        BlockPos pos = PacketBufUtil.readBlockPos(buf);

        server.execute(() ->
        {
            MotorisedBlock.DiagnosticsProvider diagnosticsProvider = MotorisedBlock.DiagnosticsProvider.LOOKUP.find(world, pos, null);
            if (diagnosticsProvider != null)
            {
                send(player, diagnosticsProvider);
            }
        });
    }

    protected static void send(ServerPlayerEntity player, @NotNull MotorisedBlock.DiagnosticsProvider diagnosticsProvider)
    {
        MotorisedBlock.Diagnostics diagnostics = diagnosticsProvider.getDiagnostics();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(diagnostics.nbt());

        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, Client::applyClient);
        }

        private static void applyClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packet)
        {
            MotorisedBlock.Diagnostics diagnostics = new MotorisedBlock.Diagnostics(buf.readNbt());
            client.execute(() ->
            {
                MachineHudOverlay.getInstance().receive(diagnostics);
            });
        }

        public static void send(World world, BlockPos pos)
        {
            if (!world.isClient())
                throw new IllegalStateException();

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeString(world.getRegistryKey().getValue().toString());
            PacketBufUtil.writeBlockPos(buf, pos);

            ClientPlayNetworking.send(ID, buf);
        }
    }
}
