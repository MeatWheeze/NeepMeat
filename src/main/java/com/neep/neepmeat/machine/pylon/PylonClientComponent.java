package com.neep.neepmeat.machine.pylon;

import com.neep.meatlib.util.ClientComponent;
import com.neep.neepmeat.client.hud.HUDOverlays;
import com.neep.neepmeat.client.sound.PylonSoundInstance;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

import static com.neep.neepmeat.machine.pylon.PylonBlockEntity.RUNNING_SPEED;

@Environment(value = EnvType.CLIENT)
public class PylonClientComponent implements ClientComponent
{
    private final PylonBlockEntity be;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PylonSoundInstance mainSound;
    private final PylonSoundInstance runningSound;

    public PylonClientComponent(PylonBlockEntity be)
    {
        this.be = be;
        this.mainSound = new PylonSoundInstance(be, be.getPos(), NMSounds.PYLON_START, SoundCategory.BLOCKS);
        this.runningSound = new PylonSoundInstance(be, be.getPos(), NMSounds.PYLON_ACTIVE, SoundCategory.BLOCKS);
    }

    protected static void causeVignette()
    {
        HUDOverlays.startPylonVignette();
    }

    @Override
    public void clientTick()
    {
        float clamped = MathHelper.clamp(be.getSpeed() - RUNNING_SPEED, 0, RUNNING_SPEED * 2) / (RUNNING_SPEED * 2);
        float threshold = MathHelper.lerp(clamped, 0f, 0.6f);
        float p = be.random.nextFloat();
        if (p < threshold)
        {
            be.getWorld().getNonSpectatingEntities(PlayerEntity.class, be.getBox()).stream().findFirst().ifPresent(pl ->
                    PylonClientComponent.causeVignette());
        }

        if (!client.getSoundManager().isPlaying(mainSound))
        {
            client.getSoundManager().play(mainSound);
        }
        if (be.isRunning() && !client.getSoundManager().isPlaying(runningSound))
        {
            client.getSoundManager().play(runningSound);
        }
        if (!be.isRunning() && client.getSoundManager().isPlaying(runningSound))
        {
            client.getSoundManager().stop(runningSound);
        }
    }
}
