package com.neep.neepmeat.neepbus;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
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
        if (world.getBlockEntity(pos) instanceof PortTestBlockEntity be)
        {
            return be.config.getInputPorts();
        }

        return NO_PORTS;
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {

    }

    public static class PortTestBlockEntity extends SyncableBlockEntity
    {

        private final SimpleInputPort inputPort = new SimpleInputPort()
        {
            @Override
            public void receive(int data)
            {
                for (PlayerEntity player : getWorld().getPlayers())
                {
                    player.sendMessage(Text.of(String.valueOf(data)), false);
                }
            }
        };

        private final NeepBusConfig config = new NeepBusConfigImpl(
                List.of(new NeepBusConfig.SimpleEntry("ooer")),
                List.of(),
                List.of(inputPort),
                inputPort::invalidateAddress,
                () -> {}
        );

        public PortTestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
