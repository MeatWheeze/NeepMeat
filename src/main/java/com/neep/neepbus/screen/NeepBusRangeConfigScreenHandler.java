package com.neep.neepbus.screen;

import com.neep.neepbus.NeepBusScreenHandlers;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepbus.util.NeepBusConfig;
import com.neep.neepbus.part.Ranged;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class NeepBusRangeConfigScreenHandler extends NeepBusConfigScreenHandler
{
    public final RangeConfigHandler rangeConfig;

    public static ExtendedScreenHandlerFactory getFactory(NeepBusConfig config, Ranged ranged)
    {
        return new ExtendedScreenHandlerFactory()
        {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
            {
                NeepBusConfigScreenHandler.writeOpeningData(config, buf);
            }

            @Override
            public Text getDisplayName()
            {
                return Text.empty();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
            {
                return new NeepBusRangeConfigScreenHandler(playerInventory, syncId, config, ranged);
            }
        };
    }

    public NeepBusRangeConfigScreenHandler(ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId, NeepBusConfig config, Ranged ranged)
    {
        super(type, playerInventory, syncId, config);
        rangeConfig = new RangeConfigHandler(playerInventory.player, ranged);
    }

    public NeepBusRangeConfigScreenHandler(PlayerInventory playerInventory, int syncId, NeepBusConfig config, Ranged ranged)
    {
        this(NeepBusScreenHandlers.NEEPBUS_RANGE_CONFIG, playerInventory, syncId, config, ranged);
    }

    public NeepBusRangeConfigScreenHandler(int syncId, PlayerInventory player, PacketByteBuf buf)
    {
        super(syncId, player, buf);
        rangeConfig = new RangeConfigHandler(playerInventory.player, Ranged.EMPTY);
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();
        rangeConfig.sendUpdates();
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        rangeConfig.close();
    }
}
