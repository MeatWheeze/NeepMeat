package com.neep.neepmeat.transport.screen_handler;

import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import org.jetbrains.annotations.Nullable;

public class LimiterValveScreenHandler extends BasicScreenHandler
{
    public static final int PROPERTIES = 2;
    public static final int PROP_MAX_AMOUNT = 0;
    public static final int PROP_MB_MODE = 1;
    protected int maxRate;

    public LimiterValveScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable PropertyDelegate delegate)
    {
        super(TransportScreenHandlers.LIMITER_VALVE, playerInventory, null, syncId, delegate);
    }

    public LimiterValveScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        super(TransportScreenHandlers.LIMITER_VALVE, playerInventory, null, syncId, new ArrayPropertyDelegate(PROPERTIES));
        setProperty(PROP_MAX_AMOUNT, buf.readVarInt());
        setProperty(PROP_MB_MODE, buf.readVarInt());
    }
}
