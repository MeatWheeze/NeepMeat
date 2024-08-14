package com.neep.neepmeat.neepbus.client;

import com.neep.neepmeat.neepbus.block.GaugeProvider;
import com.neep.neepmeat.neepbus.block.entity.GaugeBlockEntity;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class NeepBusClient
{
    public static void init()
    {
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
    }
}
