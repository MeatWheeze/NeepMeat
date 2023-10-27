package com.neep.meatweapons.client.sound;

import com.neep.meatlib.attachment.player.PlayerAttachment;
import com.neep.meatweapons.item.AssaultDrillItem;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value= EnvType.CLIENT)
public class DrillSoundInstance extends MovingSoundInstance implements PlayerAttachment
{
    protected PlayerEntity player;

    public static String ATTACHMENT_ID = "meatweapons:drill_sound";

    public static DrillSoundInstance get(PlayerEntity player)
    {
        return player.meatlib$getAttachmentManager().getAttachment(ATTACHMENT_ID);
    }

    public DrillSoundInstance(PlayerEntity player)
    {
        super(NMSounds.DRILL_RUNNING, SoundCategory.NEUTRAL, Random.create());
        this.player = player;

        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();

        MinecraftClient.getInstance().getSoundManager().play(this);
    }

    @Override
    public boolean canPlay()
    {
        return true;
//        return player.getInventory() != null
//                && (player.getStackInHand(Hand.MAIN_HAND).isOf(MWItems.ASSAULT_DRILL)
//                || player.getStackInHand(Hand.OFF_HAND).isOf(MWItems.ASSAULT_DRILL));
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

        ItemStack stack = player.getActiveItem();
        if (stack.getItem() instanceof AssaultDrillItem drill && AssaultDrillItem.using(stack))
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

    @Override
    public void tickAttachment()
    {

    }
}
