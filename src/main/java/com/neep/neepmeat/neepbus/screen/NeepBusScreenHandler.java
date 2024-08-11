package com.neep.neepmeat.neepbus.screen;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.neepbus.NeepBusConfig;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public class NeepBusScreenHandler extends BasicScreenHandler
{
    // Null on client
    @Nullable private NeepBusConfig config;

    // For extension
    protected NeepBusScreenHandler(ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId)
    {
        super(type, playerInventory, null, syncId, null);
    }

    public NeepBusScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(playerInventory, syncId, null);
//        this.config =
    }

    public NeepBusScreenHandler(PlayerInventory playerInventory, int syncId, @Nullable NeepBusConfig config)
    {
        this(ScreenHandlerInit.NEEPBUS_CONFIG, playerInventory, syncId);
        this.config = config;
    }
}
