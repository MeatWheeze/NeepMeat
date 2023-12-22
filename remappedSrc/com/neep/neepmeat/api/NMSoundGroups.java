package com.neep.neepmeat.api;

import com.neep.neepmeat.init.NMSounds;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class NMSoundGroups
{
    public static final BlockSoundGroup METAL = new BlockSoundGroup(1f, 1f, SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, SoundEvents.BLOCK_NETHERITE_BLOCK_STEP, SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundEvents.BLOCK_NETHERITE_BLOCK_HIT, SoundEvents.BLOCK_NETHERITE_BLOCK_FALL);
    public static final BlockSoundGroup MECHANICAL_MACHINE = new BlockSoundGroup(1f, 1f, NMSounds.MECHANICAL_MACHINE_BREAK, SoundEvents.BLOCK_NETHERITE_BLOCK_STEP, NMSounds.MECHANICAL_MACHINE_PLACE, SoundEvents.BLOCK_NETHERITE_BLOCK_HIT, SoundEvents.BLOCK_NETHERITE_BLOCK_FALL);
    public static final BlockSoundGroup FLESH_MACHINE = new BlockSoundGroup(1f, 1f, NMSounds.MECHANICAL_MACHINE_BREAK, SoundEvents.BLOCK_NETHERITE_BLOCK_STEP, NMSounds.FLESH_MACHINE_PLACE, SoundEvents.BLOCK_NETHERITE_BLOCK_HIT, SoundEvents.BLOCK_NETHERITE_BLOCK_FALL);
}
