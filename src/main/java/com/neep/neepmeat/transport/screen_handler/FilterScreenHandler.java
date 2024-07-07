package com.neep.neepmeat.transport.screen_handler;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FilterScreenHandler extends BasicScreenHandler
{
    public final ChannelManager<Test> channel;

    public FilterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, syncId);
    }

    public FilterScreenHandler(PlayerInventory playerInventory, int syncId)
    {
        super(ScreenHandlerInit.FILTER, playerInventory, null, syncId, null);

        channel = ChannelManager.create(
                new Identifier(NeepMeat.NAMESPACE, "filter_test"),
                ChannelFormat.builder(Test.class)
                        .param(ParamCodec.INT)
                        .param(ParamCodec.STRING)
                        .build(),
                playerInventory.player
                );
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();

        channel.emitter().apply(123, "ooer");
    }

    public void test(int i, String s)
    {
        System.out.println(s);
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        channel.close();
    }

    public interface Test
    {
        void apply(int i, String s);
    }
}
