package com.neep.neepmeat.neepbus.screen;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.neepbus.NeepBusConfig;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class NeepBusConfigScreenHandler extends BasicScreenHandler
{
    public final AddressConfigHandler configHandler;

    public static ExtendedScreenHandlerFactory getFactory(NeepBusConfig config)
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
                return new NeepBusConfigScreenHandler(playerInventory, syncId, config);
            }
        };
    }

    public static void writeOpeningData(NeepBusConfig config, PacketByteBuf buf)
    {
        AddressConfigHandler.SYNC_ENTRY_CODEC.encode(config.getInputs().stream().map(AddressConfigHandler.SyncEntry::of).toList(), buf);
        AddressConfigHandler.SYNC_ENTRY_CODEC.encode(config.getOutputs().stream().map(AddressConfigHandler.SyncEntry::of).toList(), buf);
    }

    // For extension
    protected NeepBusConfigScreenHandler(ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId, NeepBusConfig config)
    {
        super(type, playerInventory, null, syncId, null);

        this.configHandler = new AddressConfigHandler(config, playerInventory.player);
    }

    public NeepBusConfigScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, syncId, null);
        configHandler.receiveOpeningData(buf);
    }

    public NeepBusConfigScreenHandler(PlayerInventory playerInventory, int syncId, NeepBusConfig config)
    {
        this(ScreenHandlerInit.NEEPBUS_CONFIG, playerInventory, syncId, config);

    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);

        configHandler.close();
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();

        configHandler.sendUpdates();
    }


    @FunctionalInterface
    public interface OnAddressChange
    {
        void onAddressChange(boolean output, int idx, String newAddress);
    }

    @FunctionalInterface
    public interface UpdateEntries
    {
        void update(List<AddressConfigHandler.SyncEntry> entries);
    }
}
