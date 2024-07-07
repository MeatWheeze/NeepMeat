package com.neep.neepmeat.transport.screen_handler;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.Sender;
import com.neep.meatlib.network.ServerChannelSender;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FilterScreenHandler extends BasicScreenHandler
{
    public static final Identifier CHANNEL_ID = new Identifier(NeepMeat.NAMESPACE, "filter_test");
    public static final ChannelFormat<Test> FORMAT = ChannelFormat.builder(Test.class)
            .param(ParamCodec.INT)
            .param(ParamCodec.STRING)
            .build();

    private final Sender<Test> sender;

    public FilterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, syncId);
    }

    public FilterScreenHandler(PlayerInventory playerInventory, int syncId)
    {
        super(ScreenHandlerInit.FILTER, playerInventory, null, syncId, null);

        sender = new ServerChannelSender<>(CHANNEL_ID, FORMAT, playerInventory.player);
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();
        
        sender.emitter().apply(123, "ooer");
    }

    public void test(int i, String s)
    {
        System.out.println(s);
    }

    public interface Test
    {
        void apply(int i, String s);
    }
}
