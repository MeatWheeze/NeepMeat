package com.neep.neepmeat.plc.screen;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;

public class PLCScreenHandler extends ScreenHandler
{
    private final PropertyDelegate delegate;
    private final PLCBlockEntity plc;

    // Client
    public PLCScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(syncId,
                (PLCBlockEntity) playerInventory.player.world.getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(PLCBlockEntity.PLCPropertyDelegate.SIZE)
        );
    }

    // Server
    public PLCScreenHandler(int syncId, PLCBlockEntity plc, PropertyDelegate delegate)
    {
        super(ScreenHandlerInit.PLC, syncId);
        this.plc = plc;
        this.delegate = delegate;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    public PLCBlockEntity getPlc()
    {
        return plc;
    }
}
