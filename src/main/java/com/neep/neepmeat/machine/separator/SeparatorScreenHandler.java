package com.neep.neepmeat.machine.separator;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import org.jetbrains.annotations.Nullable;

public class SeparatorScreenHandler extends BasicScreenHandler
{
    public SeparatorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, syncId, new ArrayPropertyDelegate(SeparatorBlockEntity.Delegate.SIZE));

        setProperty(SeparatorBlockEntity.Properties.REMAINDER.ordinal(), buf.readInt());
        setProperty(SeparatorBlockEntity.Properties.TAKE_BABIES.ordinal(), buf.readInt());
    }

    public SeparatorScreenHandler(PlayerInventory playerInventory, int syncId, @Nullable PropertyDelegate delegate)
    {
        super(ScreenHandlerInit.SEPARATOR, playerInventory, null, syncId, delegate);
    }

    public void setRemainder(int remainder)
    {
        ScreenPropertyC2SPacket.Client.send(SeparatorBlockEntity.Properties.REMAINDER.ordinal(), remainder);
    }

    public void setTakeBabies(boolean take)
    {
        ScreenPropertyC2SPacket.Client.send(SeparatorBlockEntity.Properties.TAKE_BABIES.ordinal(), take ? 1 : 0);
    }

    public boolean takeBabies()
    {
        return getProperty(SeparatorBlockEntity.Properties.TAKE_BABIES.ordinal()) > 0;
    }
}
