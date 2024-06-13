package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.util.ClientComponent;
import com.neep.neepmeat.client.sound.BlockSoundInstance;
import com.neep.neepmeat.client.sound.LoopingSoundInstance;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class CharnelPumpClient implements ClientComponent
{
    private final CharnelPumpBlockEntity be;
    private final SoundManager soundManager;
    private final SoundInstance upSound;
    private final SoundInstance topSound;
    private final SoundInstance downSound;
    private final SoundInstance glugSound;
    private final SoundInstance idleSound;

    public CharnelPumpClient(CharnelPumpBlockEntity be)
    {
        this.be = be;
        this.soundManager = MinecraftClient.getInstance().getSoundManager();

        this.upSound = new BlockSoundInstance(NMSounds.CHARNEL_PUMP_UP, SoundCategory.BLOCKS, be.getPos().up(3));
        this.topSound = new BlockSoundInstance(NMSounds.CHARNEL_PUMP_TOP, SoundCategory.BLOCKS, be.getPos().up(3));
        this.downSound = new BlockSoundInstance(NMSounds.CHARNEL_PUMP_DOWN, SoundCategory.BLOCKS, be.getPos().up(3));
        this.glugSound = new BlockSoundInstance(NMSounds.CHARNEL_PUMP_GLUG, SoundCategory.BLOCKS, be.getPos().up(3));
        this.idleSound = new LoopingSoundInstance(NMSounds.CHARNEL_PUMP_IDLE, SoundCategory.BLOCKS, 1, 1, Random.create(), be.getPos().up(3));
    }

    @Override
    public void clientTick()
    {
        float increment = be.progressIncrement();

        if (be.getWorld().getTime() % 4 == 0)
        {
            if (be.hasAir && !CharnelPumpBlockEntity.canRun(increment))
            {
                if (!soundManager.isPlaying(idleSound))
                    soundManager.play(idleSound);
            }
            else
            {
                soundManager.stop(idleSound);
            }
        }

        // Fractional animation ticks allows the animation to run at different speeds.
        // Unfortunately, executing events at certain ticks now requires more mathematical jank.
        if (matches(be.animationTicks, 100, increment))
        {
            soundManager.stop(glugSound);
            soundManager.stop(upSound);
        }
        else if (matches(be.animationTicks, 99, increment))
        {
            soundManager.play(upSound);
        }
        else if (matches(be.animationTicks, 45, increment))
        {
            soundManager.play(topSound);
        }
        else if (matches(be.animationTicks, 40, increment))
        {
            soundManager.stop(upSound);

            soundManager.play(be.hasFluid ? glugSound : downSound);
        }
    }

    private static boolean matches(float ticks, int value, float increment)
    {
        return Math.abs(ticks - value) < Math.min(1, increment);
    }
}
