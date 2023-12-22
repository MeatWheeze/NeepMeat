package com.neep.neepmeat.transport.client;

import com.neep.neepmeat.client.renderer.ItemPipeRenderer;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.client.screen.ItemRequesterScreen;
import com.neep.neepmeat.transport.client.screen.LimiterValveScreen;
import com.neep.neepmeat.transport.network.SyncRequesterScreenS2CPacket;
import com.neep.neepmeat.transport.screen_handler.TransportScreenHandlers;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class TransportClient
{
    public static void init()
    {
        HandledScreens.register(TransportScreenHandlers.ITEM_REQUESTER_HANDLER, ItemRequesterScreen::new);
        HandledScreens.register(TransportScreenHandlers.LIMITER_VALVE, LimiterValveScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ItemTransport.ITEM_REQUESTER);
        BlockEntityRendererRegistry.register(ItemTransport.ITEM_REQUESTER_BE, ItemPipeRenderer::new);

        SyncRequesterScreenS2CPacket.Client.registerReceiver();
    }
}
