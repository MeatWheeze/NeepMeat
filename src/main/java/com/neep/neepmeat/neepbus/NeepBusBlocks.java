package com.neep.neepmeat.neepbus;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.neepbus.block.LinearLeverBlock;
import com.neep.neepmeat.neepbus.block.PortTestBlock;
import com.neep.neepmeat.neepbus.block.SenderTestBlock;
import com.neep.neepmeat.neepbus.block.VerticalGaugeBlock;
import com.neep.neepmeat.neepbus.block.entity.LinearLeverBlockEntity;
import com.neep.neepmeat.neepbus.block.entity.VerticalGaugeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

@RegisterMe(NeepMeat.NAMESPACE)
public class NeepBusBlocks
{
    public static final RegistrationContext C = new RegistrationContext(NeepMeat.NAMESPACE);
    public static final MeatlibBlockSettings GAUGE_SETTINGS = MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS).hardness(0.3f);

    public static final Block LINEAR_LEVER = new LinearLeverBlock(C, ItemSettings.block(), MeatlibBlockSettings.copyOf(GAUGE_SETTINGS));
    public static final Block VERTICAL_GAUGE = new VerticalGaugeBlock(C, ItemSettings.block(), MeatlibBlockSettings.copyOf(GAUGE_SETTINGS));
    public static final PortTestBlock PORT_TEST = new PortTestBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));
    public static final SenderTestBlock SENDER_TEST = new SenderTestBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));

    public static BlockEntityType<VerticalGaugeBlockEntity> VERTICAL_GAUGE_BE;
    public static BlockEntityType<LinearLeverBlockEntity> LINEAR_LEVER_BE;
    public static BlockEntityType<SenderTestBlock.BlockEntity> SENDER_TEST_BE;
    public static BlockEntityType<PortTestBlock.PortTestBlockEntity> PORT_TEST_BE;

    public static void init()
    {
        VERTICAL_GAUGE_BE = NMBlockEntities.register("vertical_gauge", (p, s) -> new VerticalGaugeBlockEntity(VERTICAL_GAUGE_BE, p, s), VERTICAL_GAUGE);
        LINEAR_LEVER_BE = NMBlockEntities.register("linear_lever", (p, s) -> new LinearLeverBlockEntity(LINEAR_LEVER_BE, p, s), LINEAR_LEVER);
        SENDER_TEST_BE = NMBlockEntities.register("sender_test", (p, s) -> new SenderTestBlock.BlockEntity(SENDER_TEST_BE, p, s), SENDER_TEST);
        PORT_TEST_BE = NMBlockEntities.register("port_test", (p, s) -> new PortTestBlock.PortTestBlockEntity(PORT_TEST_BE, p, s), PORT_TEST);
    }
}
