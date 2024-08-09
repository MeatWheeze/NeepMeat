package com.neep.neepmeat.machine.reactor;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.PropertyValue;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;

import java.util.Map;

@RegisterMe(NeepMeat.NAMESPACE)
public class IntrusionReactor
{
    public static final RegistrationContext C = new RegistrationContext(NeepMeat.NAMESPACE);

    public static final FabricBlockSettings FLESH_SETTINGS = MeatlibBlockSettings.create().sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS);

    // --- Reactor ---
    public static Block NERVOUS_TISSUE = new ReceiverOrganismBlock(C, Map.of(
            ReceiverOrganismStructure.Property.ORGANISATION, new PropertyValue(PropertyValue.Function.ADD, 0.1f)
    ), MeatlibBlockSettings.copyOf(FLESH_SETTINGS));

    public static Block REACTION_CORE = new ReactionCoreBlock(C, MeatlibBlockSettings.copyOf(FLESH_SETTINGS));
    public static Block ACTIVE_WASTE = new ActiveWasteBlock(C, MeatlibBlockSettings.copyOf(FLESH_SETTINGS));
}
