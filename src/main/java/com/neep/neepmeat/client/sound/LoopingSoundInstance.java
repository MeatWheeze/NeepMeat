package com.neep.neepmeat.client.sound;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class LoopingSoundInstance extends PositionedSoundInstance
{
    public LoopingSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, BlockPos pos)
    {
        super(sound, category, volume, pitch, random, pos);
        repeat = true;
    }
}
