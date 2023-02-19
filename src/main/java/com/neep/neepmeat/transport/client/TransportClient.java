package com.neep.neepmeat.transport.client;

import com.neep.neepmeat.client.renderer.ItemPipeRenderer;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemPipeBlockEntity;
import com.neep.neepmeat.transport.client.screen.ItemRequesterScreen;
import com.neep.neepmeat.transport.network.SyncRequesterScreenS2CPacket;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class TransportClient
{
    public static void init()
    {
        HandledScreens.register(ItemTransport.ITEM_REQUESTER_HANDLER, ItemRequesterScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ItemTransport.ITEM_REQUESTER);
        BlockEntityRendererRegistry.register(ItemTransport.ITEM_REQUESTER_BE, ItemPipeRenderer::new);

        SyncRequesterScreenS2CPacket.Client.registerReceiver();
    }
}
