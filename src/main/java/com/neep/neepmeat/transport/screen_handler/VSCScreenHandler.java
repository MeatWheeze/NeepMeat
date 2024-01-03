package com.neep.neepmeat.transport.screen_handler;

import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import com.neep.neepmeat.transport.block.energy_transport.entity.VSCBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import org.jetbrains.annotations.Nullable;

public class VSCScreenHandler extends BasicScreenHandler
{
    public VSCScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, null, syncId, new ArrayPropertyDelegate(VSCBlockEntity.VSCDelegate.Names.values().length));
        setProperty(VSCBlockEntity.VSCDelegate.Names.POWER_FLOW_EJ.ordinal(), buf.readInt());
    }

    public VSCScreenHandler(PlayerInventory playerInventory, @Nullable Inventory inventory, int syncId, @Nullable PropertyDelegate delegate)
    {
        super(TransportScreenHandlers.VSC, playerInventory, inventory, syncId, delegate);
    }
}
