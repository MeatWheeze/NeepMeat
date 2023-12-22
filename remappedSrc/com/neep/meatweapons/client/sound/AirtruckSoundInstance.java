package com.neep.meatweapons.client.sound;

import com.neep.meatweapons.entity.AirtruckEntity;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value= EnvType.CLIENT)
public class AirtruckSoundInstance extends MovingSoundInstance
{
    protected final AirtruckEntity entity;
    private float distance = 0.0f;
    public float maxVol = 1;

    public AirtruckSoundInstance(AirtruckEntity entity)
    {
        super(NMSounds.AIRTRUCK_RUNNING, SoundCategory.NEUTRAL, Random.create());
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

    public static void initEvent()
    {
        ClientEntityEvents.ENTITY_LOAD.register((entity1, world) ->
        {
            // If Mojang is allowed to get away with the abomination that is ClientPlayNetworkHandler then I can do this.
            if (entity1 instanceof AirtruckEntity airtruck)
            {
                MinecraftClient.getInstance().getSoundManager().play(new AirtruckSoundInstance(airtruck));
            }
        });
    }
}
