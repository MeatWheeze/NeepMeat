package com.neep.meatweapons.client.sound;

import com.neep.meatweapons.item.AssaultDrillItem;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value= EnvType.CLIENT)
public class DrillSoundInstance extends MovingSoundInstance
{
    protected PlayerEntity player;

    public DrillSoundInstance()
    {
        super(NMSounds.DRILL_RUNNING, SoundCategory.NEUTRAL, Random.create());

        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
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
        if (this.player.isRemoved())
        {
            this.setDone();
            return;
        }

        this.x = this.player.getX();
        this.y = this.player.getY();
        this.z = this.player.getZ();

        float targetVolume;
        float targetPitch;

        if (AssaultDrillItem.using(player.getMainHandStack()))
        {
            targetVolume = 1;
            targetPitch = 1;
        }
        else
        {
            targetVolume = 0;
            targetPitch = 0;
        }

        this.volume = MathHelper.lerp(0.2f, this.volume, targetVolume);
        this.pitch = MathHelper.lerp(0.2f, this.pitch, targetPitch);
    }

    public void setPlayer(PlayerEntity player)
    {
        this.player = player;
    }
}
