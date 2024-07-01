package com.neep.neepmeat.client.item;

import com.neep.meatlib.client.api.event.InputEvents;
import com.neep.meatlib.item.ClientBlockAttackListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BlockAttackListenerThings
{
    public static void init()
    {
        InputEvents.POST_INPUT.register((window, key, scancode, action, modifiers) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.options != null)
            {
                ItemStack mainStack = client.player.getMainHandStack();

                if (mainStack.getItem() instanceof ClientBlockAttackListener listener &&
                        (client.options.attackKey.matchesKey(key, scancode)
                                || client.options.attackKey.matchesMouse(key))
                )
                {
                    if (client.options.attackKey.isPressed())
                    {
                        listener.onAttackBlock(mainStack, client.player);
                    }
                    else
                    {
                        listener.onFinishAttackBlock(mainStack, client.player);
                    }
                }
            }
        });
    }
}
