package com.neep.neepbus;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepbus.block.*;
import com.neep.neepbus.block.entity.ConfigProvider;
import com.neep.neepbus.block.entity.LinearLeverBlockEntity;
import com.neep.neepbus.block.entity.VerticalGaugeBlockEntity;
import com.neep.neepbus.item.NetworkingToolItem;
import com.neep.neepbus.network.NeepBusNetwork;
import com.neep.neepbus.util.ConfigEntry;
import com.neep.neepbus.util.NeepBusConfig;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RegisterMe(NeepBus.REGISTRY_NAMESPACE)
public class NeepBus implements ModInitializer
{
    // Use the same namespace so that the content will appear when @neepmeat is used in REI/EMI
    public static final String REGISTRY_NAMESPACE = "neepmeat";

    public static final RegistrationContext C = new RegistrationContext(REGISTRY_NAMESPACE);

    public static final MeatlibBlockSettings GAUGE_SETTINGS = MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS).hardness(0.3f);

    public static final Block LINEAR_LEVER = new LinearLeverBlock(C, ItemSettings.block(), MeatlibBlockSettings.copyOf(GAUGE_SETTINGS));
    public static final Block VERTICAL_GAUGE = new VerticalGaugeBlock(C, ItemSettings.block(), MeatlibBlockSettings.copyOf(GAUGE_SETTINGS));
    public static final PortTestBlock PORT_TEST = new PortTestBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));
    public static final SenderTestBlock SENDER_TEST = new SenderTestBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));

    public static BlockEntityType<VerticalGaugeBlockEntity> VERTICAL_GAUGE_BE;
    public static BlockEntityType<LinearLeverBlockEntity> LINEAR_LEVER_BE;
    public static BlockEntityType<SenderTestBlock.BlockEntity> SENDER_TEST_BE;
    public static BlockEntityType<PortTestBlock.PortTestBlockEntity> PORT_TEST_BE;

    public static Item NETWORKING_TOOL = new NetworkingToolItem(NMItems.C, TooltipSupplier.hidden(3), new MeatlibItemSettings().group(NMItemGroups.GENERAL));

    @Override
    public void onInitialize()
    {
        VERTICAL_GAUGE_BE = NMBlockEntities.register("vertical_gauge", (p, s) -> new VerticalGaugeBlockEntity(VERTICAL_GAUGE_BE, p, s), VERTICAL_GAUGE);
        LINEAR_LEVER_BE = NMBlockEntities.register("linear_lever", (p, s) -> new LinearLeverBlockEntity(LINEAR_LEVER_BE, p, s), LINEAR_LEVER);
        SENDER_TEST_BE = NMBlockEntities.register("sender_test", (p, s) -> new SenderTestBlock.BlockEntity(SENDER_TEST_BE, p, s), SENDER_TEST);
        PORT_TEST_BE = NMBlockEntities.register("port_test", (p, s) -> new PortTestBlock.PortTestBlockEntity(PORT_TEST_BE, p, s), PORT_TEST);

        NeepBusScreenHandlers.init();

        NeepBusNetwork.NT_CONNECT.receiverHandler(EnvType.SERVER, (player, buf, responseSender) -> (pos, isOutput, entryIndex, address) ->
        {
            World world = player.getWorld();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof NeepBusProvider provider)
            {
                @Nullable NeepBusConfig config = provider.getConfig(world, pos, state);
                if (config != null)
                {
                    List<? extends ConfigEntry> entries = isOutput ? config.getOutputs() : config.getInputs();

                    if (entryIndex < entries.size())
                    {
                        entries.get(entryIndex).setAddress(address);
                        config.applyChanges();
                    }
                }
            }
        });
    }
}
