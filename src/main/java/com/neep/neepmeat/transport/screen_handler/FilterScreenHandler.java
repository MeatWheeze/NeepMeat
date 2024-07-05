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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FilterScreenHandler extends BasicScreenHandler
{
    public static final int BACKGROUND_WIDTH = 230;
    public static final int BACKGROUND_HEIGHT = 239;

    public final ChannelManager<UpdateToClient> updateToClient;
    public final ChannelManager<UpdateToServer> updateToServer;
    public final ChannelManager<AddFilter> addFilter;

    private FilterList filter;

    public FilterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(new FilterList(), playerInventory, syncId);
    }

    public FilterScreenHandler(FilterList filter, PlayerInventory playerInventory, int syncId)
    {
        super(ScreenHandlerInit.FILTER, playerInventory, null, syncId, null);
        this.filter = filter;

        createInventory(8, BACKGROUND_HEIGHT - 80, playerInventory);
        createHotbar(   8, BACKGROUND_HEIGHT - 23, playerInventory);

        updateToServer = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_to_server"),
            ChannelFormat.builder(UpdateToServer.class)
                    .param(ParamCodec.INT)
                    .param(ParamCodec.NBT)
                    .build(),
                playerInventory.player
                );

        updateToClient = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_to_client"),
                ChannelFormat.builder(UpdateToClient.class)
                        .param(FilterList.CODEC)
                        .build(),
                playerInventory.player
                );

        addFilter = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "add_filter"),
                ChannelFormat.builder(AddFilter.class)
                        .param(ParamCodec.IDENTIFIER)
                        .build(),
                playerInventory.player
        );

        updateToServer.receiver(this::updateToServer);
        addFilter.receiver(this::addFilter);
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();

        updateToClient.emitter().apply(filter);
    }

    public void updateToClient(FilterList filter)
    {
        this.filter = filter;
    }

    public void updateToServer(int index, NbtCompound nbt)
    {
        filter.getEntries().get(index).update(nbt);
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        updateToClient.close();
        updateToServer.close();
        addFilter.close();
        super.onClosed(player);
    }

    public FilterList getFilters()
    {
        return filter;
    }

    public void addFilter(Filter.Constructor<?> filter)
    {
        Identifier id = Filter.REGISTRY.getId(filter);
        addFilter.emitter().apply(id);
    }

    private void addFilter(Identifier id)
    {
        Filter.Constructor<?> constructor = Filter.REGISTRY.get(id);
        if (constructor != null) // Don't trust client
        {
            Filter filter = constructor.create();
            this.filter.add(filter);
        }
    }

    public interface UpdateToClient
    {
        void apply(FilterList filterList);
    }

    public interface UpdateToServer
    {
        void apply(int index, NbtCompound nbt);
    }

    public interface AddFilter
    {
        void apply(Identifier id);
    }
}
