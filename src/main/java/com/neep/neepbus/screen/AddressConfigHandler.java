package com.neep.neepbus.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.api.network.RecordParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.meatlib.screen.WidgetHandler;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepbus.util.NeepBusConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class AddressConfigHandler implements WidgetHandler
{
    public static final ParamCodec<List<SyncEntry>> SYNC_ENTRY_CODEC = ParamCodec.list(SyncEntry.PARAM_CODEC);
    public static final ChannelFormat<NeepBusConfigScreenHandler.UpdateEntries> SYNC_ENTRIES_FORMAT = ChannelFormat.builder(NeepBusConfigScreenHandler.UpdateEntries.class)
            .param(SYNC_ENTRY_CODEC).build();

    public final ChannelManager<NeepBusConfigScreenHandler.OnAddressChange> addressChangeC2S;
    public final ChannelManager<NeepBusConfigScreenHandler.UpdateEntries> updateInputsS2C;
    public final ChannelManager<NeepBusConfigScreenHandler.UpdateEntries> updateOutputsS2C;

    // Populated in the client constructor
    public List<AddressConfigHandler.SyncEntry> outputs = List.of();
    public List<AddressConfigHandler.SyncEntry> inputs = List.of();

    protected final NeepBusConfig config;

    protected AddressConfigHandler(NeepBusConfig config, PlayerEntity player)
    {
        this.config = config;

        this.addressChangeC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "address_change"),
                ChannelFormat.builder(NeepBusConfigScreenHandler.OnAddressChange.class)
                        .param(ParamCodec.BOOLEAN)
                        .param(ParamCodec.INT)
                        .param(ParamCodec.STRING)
                        .build(),
                player);

        this.updateInputsS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_entries_inputs"),
                SYNC_ENTRIES_FORMAT,
                player);

        this.updateOutputsS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_entries_outputs"),
                SYNC_ENTRIES_FORMAT,
                player);

        addressChangeC2S.receiver(this::onAddressChange);
    }

    public void receiveOpeningData(PacketByteBuf buf)
    {
        receiveInputs(AddressConfigHandler.SYNC_ENTRY_CODEC.decode(buf));
        receiveOutputs(AddressConfigHandler.SYNC_ENTRY_CODEC.decode(buf));

        updateInputsS2C.receiver(this::receiveInputs);
        updateOutputsS2C.receiver(this::receiveOutputs);
    }

    private void receiveInputs(List<SyncEntry> list)
    {
        this.inputs = list;
    }

    private void receiveOutputs(List<SyncEntry> list)
    {
        this.outputs = list;
    }

    private void onAddressChange(boolean output, int idx, String newAddress)
    {
        if (config != null) // Just in case // REMOVE
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
    public void close()
    {
        addressChangeC2S.close();
        updateInputsS2C.close();
        updateOutputsS2C.close();
    }

    public void sendUpdates()
    {
        if (config != null)
        {
            updateInputsS2C.emitter().update(config.getInputs().stream().map(AddressConfigHandler.SyncEntry::of).toList());
            updateOutputsS2C.emitter().update(config.getOutputs().stream().map(AddressConfigHandler.SyncEntry::of).toList());
        }
    }

    public NeepBusConfig config()
    {
        return config;
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
