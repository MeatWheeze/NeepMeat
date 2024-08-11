package com.neep.neepmeat.neepbus;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class PortTestBlock extends BaseBlock implements NeepBusProvider, DataCable
{
    public PortTestBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, settings);
    }

    @Override
    public Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state)
    {
        return Map.of("ooer", data ->
        {
            for (PlayerEntity player : world.getPlayers())
            {
                player.sendMessage(Text.of(String.valueOf(data)), false);
            }
        });
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {

    }
}
