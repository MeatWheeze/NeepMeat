package com.neep.neepmeat.machine.phage_ray;

import com.neep.meatlib.api.event.KeyboardEvents;
import com.neep.meatlib.client.api.event.UseAttackCallback;
import com.neep.meatlib.util.ClientComponent;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class PhageRayClientComponent implements ClientComponent
{
    private static boolean prevUse;

    private final TrackingSoundInstance runningInstance;
    private final PhageRayEntity parent;

    public PhageRayClientComponent(PhageRayEntity parent)
    {
        runningInstance = new TrackingSoundInstance(
                NMSounds.PHAGE_RAY_RUNNING, SoundCategory.BLOCKS,
                16, 1,
                parent);
        this.parent = parent;
    }

    public static void init()
    {
        KeyboardEvents.POST_INPUT.register((window, key, scancode, action, modifiers) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null)
                return;

            if (client.player.getVehicle() instanceof PhageRayEntity phageRay)
            {
                if (client.options.useKey.isPressed())
                {
                    if (!prevUse)
                    {
                        phageRay.setPlayerTrigger(true);
                        sendPacket(client.player, true);
                        prevUse = true;
                    }
                }
                else
                {
                    if (prevUse)
                    {
                        phageRay.setPlayerTrigger(false);
                        sendPacket(client.player, false);
                        prevUse = false;
                    }
                }
            }
        });

        UseAttackCallback.DO_USE.register(client ->
        {
            if (client.player.getVehicle() instanceof PhageRayEntity phageRay)
            {
                return false;
            }
            return true;
        });
    }

    private static void sendPacket(PlayerEntity player, boolean trigger)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBoolean(trigger);

        ClientPlayNetworking.send(PhageRayEntity.CHANNEL_ID, buf);
    }

    @Override
    public void clientTick()
    {
        SoundManager manager = MinecraftClient.getInstance().getSoundManager();
        if (parent.isRunning() && parent.trigger && parent.triggerTicks == 0)
        {
            manager.play(new EntityTrackingSoundInstance(NMSounds.PHAGE_RAY_CHARGE, SoundCategory.BLOCKS, 16, 1, parent, 0));
        }

        if (parent.isRunning() && parent.triggerTicks >= 20 && !manager.isPlaying(runningInstance))
        {
            manager.play(runningInstance);
        }
        else if (parent.triggerTicks == 0 && manager.isPlaying(runningInstance))
        {
            manager.stop(runningInstance);
        }
    }

    @Environment(EnvType.CLIENT)
    private static class TrackingSoundInstance extends EntityTrackingSoundInstance
    {
        public TrackingSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity)
        {
            super(sound, category, volume, pitch, entity, 0);
            this.repeat = true;
            this.repeatDelay = 0;
        }
    }
}
