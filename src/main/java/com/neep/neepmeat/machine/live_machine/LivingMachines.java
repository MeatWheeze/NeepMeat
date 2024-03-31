package com.neep.neepmeat.machine.live_machine;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.live_machine.TestLivingMachineBE;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.live_machine.block.CrusherSegmentBlock;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.entity.BlockEntityType;

import static com.neep.neepmeat.init.NMBlockEntities.register;

public class LivingMachines
{
    public static final BigBlock<CrusherSegmentBlock.CrusherSegmentStructureBlock> CRUSHER_SEGMENT = BlockRegistry.queue(new CrusherSegmentBlock("crusher_segment", FabricBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS), ItemSettings.block()));
    public static BlockEntityType<CrusherSegmentBlockEntity> CRUSHER_SEGMENT_BE;

    public static BlockEntityType<TestLivingMachineBE> TEST_LIVING_MACHINE_BE;

    public static void init()
    {
        CRUSHER_SEGMENT_BE = register("crusher_segment", (p, s) -> new CrusherSegmentBlockEntity(CRUSHER_SEGMENT_BE, p, s), CRUSHER_SEGMENT);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.CRUSHER_SEGMENT_BE);

        TEST_LIVING_MACHINE_BE = register("test_living_machine", (p, s) -> new TestLivingMachineBE(LivingMachines.TEST_LIVING_MACHINE_BE, p, s), NMBlocks.TEST_LIVING_MACHINE);
    }
}
