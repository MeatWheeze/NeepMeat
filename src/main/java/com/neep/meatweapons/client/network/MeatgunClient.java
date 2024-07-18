package com.neep.meatweapons.client.network;

import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.meatgun.MeatgunAnimationManager;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.network.MeatgunNetwork;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class MeatgunClient
{
    public static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(MeatgunNetwork.CHANNEL, (client, handler, buf, responseSender) ->
        {
            MeatgunNetwork.RecoilDirection direction = MeatgunNetwork.RecoilDirection.values()[buf.readInt()];
            float amount = buf.readFloat();
            float horAmount = buf.readFloat();
            float returnSpeed = buf.readFloat();
            float horReturnSpeed = buf.readFloat();

            client.execute(() ->
            {
                MeatgunComponent component = MWComponents.MEATGUN.getNullable(client.player.getMainHandStack());
                if (component != null)
                {
                    component.getRecoil().set(direction, amount, horAmount, returnSpeed, horReturnSpeed);
                }
            });
        });

        MeatgunNetwork.SEND_ANIMATION.receiver(MeatgunClient::receiveAnimation);
    }

    private static void receiveAnimation(String name, PacketByteBuf buf)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        MeatgunComponent component1 = MWComponents.MEATGUN.getNullable(client.player.getMainHandStack());
        MeatgunComponent component2 = MWComponents.MEATGUN.getNullable(client.player.getOffHandStack());
        if (component1 != null)
        {
            MeatgunAnimationManager animationManager = component1.getAnimationManager();
            if (animationManager != null)
                animationManager.queue(name, buf);
        }
        if (component2 != null)
        {
            MeatgunAnimationManager animationManager = component2.getAnimationManager();
            if (animationManager != null)
                animationManager.queue(name, buf);
        }
    }
}
