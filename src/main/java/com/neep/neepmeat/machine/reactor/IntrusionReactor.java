package com.neep.neepmeat.machine.reactor;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.live_machine.PropertyValue;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.reactor.disruptor.DisruptorNeedleBlock;
import com.neep.neepmeat.machine.reactor.disruptor.DisruptorSegmentBlock;
import com.neep.neepmeat.machine.reactor.disruptor.DisruptorSegmentBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;

import java.util.Map;

@RegisterMe(NeepMeat.NAMESPACE)
public class IntrusionReactor
{
    public static final RegistrationContext C = new RegistrationContext(NeepMeat.NAMESPACE);

    public static final FabricBlockSettings FLESH_SETTINGS = MeatlibBlockSettings.create().sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS);

    // --- Reactor ---
    public static final Block NERVOUS_TISSUE = new ReceiverOrganismBlock(C, Map.of(
            ReceiverOrganismStructure.Property.ORGANISATION, new PropertyValue(PropertyValue.Function.ADD, 0.1f)
    ), MeatlibBlockSettings.copyOf(FLESH_SETTINGS));

    public static final Block DISRUPTOR_NEEDLE = new DisruptorNeedleBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));
    public static final Block REACTION_CORE = new ReactionCoreBlock(C, MeatlibBlockSettings.copyOf(FLESH_SETTINGS));
    public static final Block ACTIVE_WASTE = new ActiveWasteBlock(C, MeatlibBlockSettings.copyOf(FLESH_SETTINGS));

    public static final Block DISRUPTOR_SEGMENT = new DisruptorSegmentBlock(C, MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS),
            ItemSettings.block().tooltip(LivingMachineComponents.tooltip(LivingMachineComponents.DISRUPTOR_SEGMENT)));

    public static BlockEntityType<DisruptorSegmentBlockEntity> DISRUPTOR_SEGMENT_BE;

    public static void init()
    {
        DISRUPTOR_SEGMENT_BE = NMBlockEntities.register(
            "disruptor_segment", (p, s) -> new DisruptorSegmentBlockEntity(DISRUPTOR_SEGMENT_BE, p, s), DISRUPTOR_SEGMENT);

        LivingMachineComponent.LOOKUP.registerSelf(DISRUPTOR_SEGMENT_BE);
    }
}
