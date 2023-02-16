package com.neep.neepmeat.transport.client;

import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.client.screen.ItemRequesterScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class TransportClient
{
    public static void init()
    {
        HandledScreens.register(ItemTransport.ITEM_REQUESTER_HANDLER, ItemRequesterScreen::new);
    }
}
