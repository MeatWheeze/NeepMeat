package com.neep.neepmeat.client.sound;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class BlockSoundInstance extends AbstractSoundInstance
{
    public BlockSoundInstance(SoundEvent sound, SoundCategory category, BlockPos pos)
    {
        super(sound, category, Random.create());
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
    }
}
