package com.neep.neepmeat.client.sound;

import com.neep.neepmeat.machine.pylon.PylonBlockEntity;
import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PylonSoundInstance extends AbstractSoundInstance implements TickableSoundInstance
{
    protected final Identifier startId;
    protected final Identifier runningId;
    protected Sound startSound;
    protected Sound runningSound;
    private boolean done;
    private final PylonBlockEntity blockEntity;

    public PylonSoundInstance(PylonBlockEntity be, BlockPos pos, SoundEvent startSound, SoundEvent sound, SoundCategory category)
    {
        super(startSound, category);
        this.blockEntity = be;

        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.0f;
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
        this.startId = startSound.getId();
        this.runningId = sound.getId();
    }

    public boolean isRunning()
    {
        return blockEntity.getSpeed() >= PylonBlockEntity.RUNNING_SPEED;
    }

    @Override
    public Identifier getId()
    {
        return isRunning() ? this.runningId : this.startId;
    }

    @Override
    public WeightedSoundSet getSoundSet(SoundManager soundManager)
    {
        WeightedSoundSet startSet = soundManager.get(this.startId);
        this.startSound = startSet == null ? SoundManager.MISSING_SOUND : startSet.getSound();
        WeightedSoundSet runningSet = soundManager.get(this.runningId);
        this.runningSound = runningSet == null ? SoundManager.MISSING_SOUND : runningSet.getSound();
        return runningSet;
    }

    @Override
    public Sound getSound()
    {
        Sound sound =  this.isRunning() ? runningSound : startSound;
        return sound;
    }

    @Override
    public boolean isDone()
    {
        return done;
    }

    @Override
    public float getVolume()
    {
        return this.volume * getSound().getVolume();
    }

    @Override
    public float getPitch()
    {
        return this.pitch * getSound().getPitch();
    }

    @Override
    public boolean canPlay()
    {
        return true;
    }

    @Override
    public boolean shouldAlwaysPlay()
    {
        return true;
    }

    @Override
    public void tick()
    {
        this.pitch = MathHelper.lerp(0.2f, this.pitch, MathHelper.lerp(MathHelper.clamp(blockEntity.getSpeed(), 0, PylonBlockEntity.RUNNING_SPEED) / PylonBlockEntity.RUNNING_SPEED, 0, 1));
        this.volume = pitch;
        if (this.blockEntity.isRemoved())
        {
            this.done = true;
            return;
        }

    }
}
