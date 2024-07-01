package com.neep.neepmeat.client.item;

import com.neep.neepmeat.client.sound.RockDrillSoundInstance;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.random.Random;

public class RockDrillItemClient
{
    private static final RockDrillSoundInstance SOUND = new RockDrillSoundInstance(NMSounds.ROCK_DRILL, SoundCategory.PLAYERS, Random.create());

    public static void init()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            SoundManager manager = client.getSoundManager();
            PlayerEntity player = client.player;
            if (player != null)
            {
                ItemStack mainStack = player.getMainHandStack();
                if (mainStack.isOf(NMItems.ROCK_DRILL)
//                        && RockDrillItem.using(mainStack)
//                        && client.interactionManager.isBreakingBlock()
                )
                {
                    if (!manager.isPlaying(SOUND))
                    {
                        SOUND.setPlayer(player);
                        manager.play(SOUND);
                    }
                }
                else
                {
                    manager.stop(SOUND);
                }
            }

        });
    }
}
