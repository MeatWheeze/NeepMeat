package com.neep.neepmeat.transport.screen_handler;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.item.filter.Filter;
import com.neep.neepmeat.item.filter.FilterList;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class FilterScreenHandler extends BasicScreenHandler
{
    public final ChannelManager<ReceiveFilter> channel;

    private FilterList filter;

    public FilterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, syncId);
    }

    public FilterScreenHandler(PlayerInventory playerInventory, int syncId)
    {
        super(ScreenHandlerInit.FILTER, playerInventory, null, syncId, null);

        channel = ChannelManager.create(
                new Identifier(NeepMeat.NAMESPACE, "receive_filter"),
                ChannelFormat.builder(ReceiveFilter.class)
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

    public void receiveFilter(int i, String s)
    {
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        channel.close();
    }

    public FilterList getFilters()
    {
        return filter;
    }

    public interface ReceiveFilter
    {
        void apply(int i, String s);
    }
}
