package com.neep.meatlib.client.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface AppendTooltipEvent
{
    Event<AppendTooltipEvent> EVENT = EventFactory.createArrayBacked(AppendTooltipEvent.class,
            (listeners) -> (stack, world, context) ->
            {
                for (AppendTooltipEvent listener : listeners)
                {
                    listener.onAppendTooltip(stack, world, context);
                }
            });

    void onAppendTooltip(ItemStack stack, @Nullable World world, TooltipContext context);
}
