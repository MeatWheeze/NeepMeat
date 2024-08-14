package com.neep.neepbus.client;

import com.neep.neepbus.NeepBusScreenHandlers;
import com.neep.neepbus.block.GaugeProvider;
import com.neep.neepbus.block.entity.GaugeBlockEntity;
import com.neep.neepbus.client.item.NetworkingToolClient;
import com.neep.neepbus.client.screen.NeepBusConfigScreen;
import com.neep.neepbus.client.screen.NeepBusRangeConfigScreen;
import com.neep.neepbus.client.screen.SliderScreen;
import com.neep.neepbus.screen.NeepBusConfigScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class NeepBusClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        NetworkingToolClient.init();

        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            HitResult hitResult = client.crosshairTarget;

            if (client.world != null && hitResult instanceof BlockHitResult blockHitResult)
            {
                BlockPos pos = blockHitResult.getBlockPos();
                BlockState state = client.world.getBlockState(blockHitResult.getBlockPos());
                if (state.getBlock() instanceof GaugeProvider gaugeProvider)
                {
                    GaugeBlockEntity gauge = gaugeProvider.getBlockEntity(client.world, pos);
                    MutableText text = Text.empty();

                    @Nullable Text name = gauge.getName();
                    if (name != null)
                    {
                        text.append(name);
                        text.append(": ");
                    }

                    text.append(String.valueOf(gauge.getValue()));

                    client.player.sendMessage(text, true);
                }
            }
        });

        // IDE says this is unnecessary, but the compiler disagrees.
        HandledScreens.<NeepBusConfigScreenHandler, NeepBusConfigScreen<NeepBusConfigScreenHandler>>register(NeepBusScreenHandlers.NEEPBUS_CONFIG, NeepBusConfigScreen::new);
        HandledScreens.register(NeepBusScreenHandlers.NEEPBUS_RANGE_CONFIG, NeepBusRangeConfigScreen::new);
        HandledScreens.register(NeepBusScreenHandlers.SLIDER, SliderScreen::new);
    }
}
