package com.neep.neepmeat.screen_handler;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.item.filter.FilterList;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedRouterScreenHandler extends BasicScreenHandler
{
    private final List<FilterList> filters;
    private List<Boolean> notEmpty = new ArrayList<>(Arrays.asList(new Boolean[6]));

    public final ChannelManager<OpenFilter> openFilterC2S;
    public final ChannelManager<UpdateFilters> updateFiltersS2C;

    // Client
    public AdvancedRouterScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, DefaultedList.ofSize(6, new FilterList(0)));

        updateFiltersS2C.receiver(this::onUpdateFilters);
    }

    // Server
    public AdvancedRouterScreenHandler(int syncId, PlayerInventory playerInventory, List<FilterList> filters)
    {
        super(ScreenHandlerInit.ADVANCED_ROUTER, playerInventory, null, syncId, null);
        this.filters = filters;

        openFilterC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "open_filter"),
                ChannelFormat.builder(OpenFilter.class).param(ParamCodec.INT).build(),
                playerInventory.player);

        updateFiltersS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_filters"),
                ChannelFormat.builder(UpdateFilters.class).param(ParamCodec.list(ParamCodec.BOOLEAN)).build(),
                playerInventory.player);

        openFilterC2S.receiver(this::onOpenFilter);
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();

        updateFiltersS2C.emitter().apply(filters.stream().map(f -> !f.isEmpty()).toList());
    }

    private void onOpenFilter(int index)
    {
        getPlayer().openHandledScreen(new FilterScreenHandler.Factory(filters.get(index)));
    }

    private void onUpdateFilters(List<Boolean> list)
    {
        notEmpty = list;
    }

    public PlayerEntity getPlayer()
    {
        return playerInventory.player;
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        openFilterC2S.close();
        updateFiltersS2C.close();
    }

    public boolean notEmpty(Direction direction)
    {
        int index = direction.ordinal();

        // Check just in case
        return index < notEmpty.size() && notEmpty.get(direction.ordinal());
    }

    public interface OpenFilter
    {
        void apply(int index);
    }

    public interface UpdateFilters
    {
        void apply(List<Boolean> notEmpty);
    }
}

