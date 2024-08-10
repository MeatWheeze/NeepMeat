package com.neep.neepmeat.neepbus;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.entity.BlockEntityType;

@RegisterMe(NeepMeat.NAMESPACE)
public class NeepBusBlocks
{
    public static final RegistrationContext C = new RegistrationContext(NeepMeat.NAMESPACE);

    public static final PortTestBlock PORT_TEST = new PortTestBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));
    public static final SenderTestBlock SENDER_TEST = new SenderTestBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));

    public static BlockEntityType<SenderTestBlock.BlockEntity> SENDER_TEST_BE;

    public static void init()
    {
        SENDER_TEST_BE = NMBlockEntities.register("sender_test", (p, s) -> new SenderTestBlock.BlockEntity(SENDER_TEST_BE, p, s), SENDER_TEST);
    }
}
