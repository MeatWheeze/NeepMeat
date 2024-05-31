package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;

public class DisplayPlateScreenHandler extends BasicScreenHandler
{
    private final int initialCapacity;

    public DisplayPlateScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf byteBuf)
    {
        this(inventory, syncId, new ArrayPropertyDelegate(1), byteBuf.readInt());
    }

    public DisplayPlateScreenHandler(PlayerInventory playerInventory, int syncId, PropertyDelegate delegate, int initialCapacity)
    {
        super(ScreenHandlerInit.DISPLAY_PLATE, playerInventory, null, syncId, delegate);
        this.initialCapacity = initialCapacity;
    }

    public int getCapacity()
    {
        return initialCapacity;
    }

    public void setCapacity(int capacity)
    {
        ScreenPropertyC2SPacket.Client.send(0, capacity);
    }
}
