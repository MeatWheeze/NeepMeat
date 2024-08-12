package com.neep.neepmeat.neepbus.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleConsumer;

public class InteractiveControlScreenHandler extends BasicScreenHandler
{
    public final ChannelManager<MouseScroll> mouseScrollC2S;
    private final DoubleConsumer onScroll;

    protected InteractiveControlScreenHandler(@Nullable ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId, DoubleConsumer onScroll)
    {
        super(type, playerInventory, null, syncId, null);

        mouseScrollC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "mouse_scroll"),
                ChannelFormat.builder(MouseScroll.class).param(ParamCodec.DOUBLE).build(),
                playerInventory.player);
        this.onScroll = onScroll;

        mouseScrollC2S.receiver(this::mouseScroll);
    }

    public InteractiveControlScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(ScreenHandlerInit.INTERACTIVE_CONTROL, playerInventory, syncId, d -> {});
    }

    public InteractiveControlScreenHandler(int syncId, PlayerInventory playerInventory, DoubleConsumer onScroll)
    {
        this(ScreenHandlerInit.INTERACTIVE_CONTROL, playerInventory, syncId, onScroll);
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        mouseScrollC2S.close();
    }

    private void mouseScroll(double amount)
    {
        onScroll.accept(amount);
    }

    @FunctionalInterface
    public interface MouseScroll
    {
        void scroll(double amount);
    }
}
