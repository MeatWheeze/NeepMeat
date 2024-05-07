package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.init.ScreenHandlerInit;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LivingMachineScreenHandler extends BasicScreenHandler
{
    public static final Identifier GRAPH_SYNC_ID = new Identifier(NeepMeat.NAMESPACE, "lm_graph_sync");

    private final LivingMachineBlockEntity be;

    public LivingMachineScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, syncId, (LivingMachineBlockEntity) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public LivingMachineScreenHandler(PlayerInventory playerInventory, int syncId, LivingMachineBlockEntity be)
    {
        super(ScreenHandlerInit.LIVING_MACHINE, playerInventory, null, syncId, null);
        this.be = be;
    }

    @Override
    public void sendContentUpdates()
    {
        // Called every tick and in other circumstances
        super.sendContentUpdates();

        if (playerInventory.player instanceof ServerPlayerEntity serverPlayer && serverPlayer.getWorld().getTime() % 20 * 8 == 0)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            be.getDataLog().write(buf);
            ServerPlayNetworking.send(serverPlayer, GRAPH_SYNC_ID, buf);
        }
    }

    public LivingMachineBlockEntity getBlockEntity()
    {
        return be;
    }
}
