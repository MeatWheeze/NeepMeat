package com.neep.meatweapons.client.sound;

import com.neep.meatweapons.entity.AirtruckEntity;
import com.neep.neepmeat.init.SoundInitialiser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class AirtruckSoundInstance extends MovingSoundInstance
{
    protected final AirtruckEntity entity;
    private float distance = 0.0f;
    public float maxVol = 1;

    public AirtruckSoundInstance(AirtruckEntity entity)
    {
        super(SoundInitialiser.AIRTRUCK_RUNNING, SoundCategory.NEUTRAL);
//        super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
        this.entity = entity;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
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
        if (this.entity.isRemoved())
        {
            this.setDone();
            return;
        }

        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
        float f = (float) this.entity.getVelocity().horizontalLength();

        if (f >= 0.01f)
        {
            this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
            this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0f, 1f), 0.0f, 3f);
            this.pitch = MathHelper.lerp(MathHelper.clamp(f, 0.0f, 0.5f), 0.7f, 1.4f);
        } else
        {
            this.distance = 0.0f;
            this.volume = 0.0f;
        }

    }
}
