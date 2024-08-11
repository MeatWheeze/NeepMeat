package com.neep.neepmeat.neepbus.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.api.network.RecordParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.neepbus.NeepBusConfig;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NeepBusScreenHandler extends BasicScreenHandler
{
    public static final ParamCodec<List<SyncEntry>> SYNC_ENTRY_CODEC = ParamCodec.list(SyncEntry.PARAM_CODEC);
    public static final ChannelFormat<UpdateEntries> SYNC_ENTRIES_FORMAT = ChannelFormat.builder(UpdateEntries.class)
            .param(SYNC_ENTRY_CODEC).build();

    public final ChannelManager<OnAddressChange> addressChangeC2S;
    public final ChannelManager<UpdateEntries> updateInputsS2C;
    public final ChannelManager<UpdateEntries> updateOutputsS2C;

    // Null on client
    @Nullable private NeepBusConfig config;

    // Populated in the client constructor
    public List<SyncEntry> outputs = List.of();
    public List<SyncEntry> inputs = List.of();

    public static void writeOpeningData(NeepBusConfig config, PacketByteBuf buf)
    {
        SYNC_ENTRY_CODEC.encode(config.getInputs().stream().map(SyncEntry::of).toList(), buf);
        SYNC_ENTRY_CODEC.encode(config.getOutputs().stream().map(SyncEntry::of).toList(), buf);
    }

    // For extension
    protected NeepBusScreenHandler(ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId)
    {
        super(type, playerInventory, null, syncId, null);

        this.addressChangeC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "address_change"),
                ChannelFormat.builder(OnAddressChange.class)
                        .param(ParamCodec.BOOLEAN)
                        .param(ParamCodec.INT)
                        .param(ParamCodec.STRING)
                        .build(),
                playerInventory.player);

        this.updateInputsS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_entries_inputs"),
                SYNC_ENTRIES_FORMAT,
                playerInventory.player);

        this.updateOutputsS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_entries_outputs"),
                SYNC_ENTRIES_FORMAT,
                playerInventory.player);
    }

    public NeepBusScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(playerInventory, syncId, null);

        receiveInputs(SYNC_ENTRY_CODEC.decode(buf));
        receiveOutputs(SYNC_ENTRY_CODEC.decode(buf));

        updateInputsS2C.receiver(this::receiveInputs);
        updateOutputsS2C.receiver(this::receiveOutputs);
    }

    public NeepBusScreenHandler(PlayerInventory playerInventory, int syncId, NeepBusConfig config)
    {
        this(ScreenHandlerInit.NEEPBUS_CONFIG, playerInventory, syncId);
        this.config = config;

        addressChangeC2S.receiver(this::onAddressChange);
    }

    private void onAddressChange(boolean output, int idx, String newAddress)
    {
        if (config != null) // Just in case
        {
            if (!output)
            {
                config.getInputs().get(idx).setAddress(newAddress);
            }
            else
            {
                config.getOutputs().get(idx).setAddress(newAddress);
            }
        }
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);

        addressChangeC2S.close();
        updateInputsS2C.close();
        updateOutputsS2C.close();
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();

        if (config != null)
        {
            updateInputsS2C.emitter().update(config.getInputs().stream().map(SyncEntry::of).toList());
            updateOutputsS2C.emitter().update(config.getOutputs().stream().map(SyncEntry::of).toList());
        }
    }

    private void receiveInputs(List<SyncEntry> list)
    {
        this.inputs = list;
    }

    private void receiveOutputs(List<SyncEntry> list)
    {
        this.outputs = list;
    }

    @FunctionalInterface
    public interface OnAddressChange
    {
        void onAddressChange(boolean output, int idx, String newAddress);
    }

    @FunctionalInterface
    public interface UpdateEntries
    {
        void update(List<SyncEntry> entries);
    }

    // A simple container for a name and address that can be easily serialised
    public record SyncEntry(String name, String address)
    {
        public static SyncEntry of(NeepBusConfig.Entry entry)
        {
            return new SyncEntry(entry.getName(), entry.getAddress());
        }

        public static final ParamCodec<SyncEntry> PARAM_CODEC = RecordParamCodec.builder(SyncEntry.class)
                .param(ParamCodec.STRING).param(ParamCodec.STRING).build();
    }
}
