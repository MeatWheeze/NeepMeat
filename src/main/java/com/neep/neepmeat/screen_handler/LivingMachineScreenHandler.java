package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

public class LivingMachineScreenHandler extends BasicScreenHandler
{
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

    public LivingMachineBlockEntity getBlockEntity()
    {
        return be;
    }
}
