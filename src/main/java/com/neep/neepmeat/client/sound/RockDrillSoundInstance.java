package com.neep.neepmeat.client.sound;

import com.neep.neepmeat.item.RockDrillItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class RockDrillSoundInstance extends MovingSoundInstance
{
    @Nullable protected PlayerEntity player;

    public RockDrillSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, Random random)
    {
        super(soundEvent, soundCategory, random);
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.5f;
        this.pitch = 1;
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
        if (this.player != null && this.player.isRemoved())
        {
            this.setDone();
            return;
        }

        if (player != null)
        {

            ItemStack mainStack = player.getMainHandStack();
            if (RockDrillItem.using(mainStack) && MinecraftClient.getInstance().crosshairTarget.getType() == HitResult.Type.BLOCK)
            {
//                        && RockDrillItem.using(mainStack)
//                        && client.interactionManager.isBreakingBlock()
//                this.volume = 0.5f;
            }
            else
            {
                this.volume = MathHelper.lerp(0.7f, this.volume, 0);
            }
        }
        else
        {
            this.volume = 0;
        }


        this.x = this.player.getX();
        this.y = this.player.getY();
        this.z = this.player.getZ();
    }

    public void setPlayer(PlayerEntity player)
    {
        this.player = player;
    }
}
