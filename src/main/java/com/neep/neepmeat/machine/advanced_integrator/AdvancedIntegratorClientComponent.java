package com.neep.neepmeat.machine.advanced_integrator;

import com.neep.meatlib.util.ClientComponent;
import com.neep.neepmeat.client.sound.BlockSoundInstance;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class AdvancedIntegratorClientComponent implements ClientComponent
{
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final BlockSoundInstance sound;

    public AdvancedIntegratorClientComponent(AdvancedIntegratorBlockEntity be)
    {
        this.sound = new BlockSoundInstance(NMSounds.ADVANCED_INTEGRATOR_AMBIENT, SoundCategory.BLOCKS, be.getPos().up(3));
    }

    @Override
    public void clientTick()
    {
        if (!client.getSoundManager().isPlaying(sound))
        {
            client.getSoundManager().play(sound);
        }
    }
}
