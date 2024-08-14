package com.neep.neepmeat.machine.reactor.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.machine.reactor.ReceiverOrganismComponent;
import com.neep.neepmeat.machine.reactor.ReceiverOrganismComponentProvider;
import com.neep.neepmeat.machine.reactor.block.entity.CoreSensorBlockEntity;
import com.neep.neepmeat.machine.reactor.IntrusionReactor;
import com.neep.neepmeat.neepbus.NeepBusPort;
import com.neep.neepmeat.neepbus.NeepBusProvider;
import com.neep.neepmeat.neepbus.screen.NeepBusConfigScreenHandler;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CoreSensorBlock extends BaseBlock implements BlockEntityProvider, ReceiverOrganismComponentProvider, NeepBusProvider, DataCable
{
    public CoreSensorBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return IntrusionReactor.CORE_SENSOR_BE.instantiate(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof CoreSensorBlockEntity be)
        {
            player.openHandledScreen(NeepBusConfigScreenHandler.getFactory(be.getConfig()));
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof CoreSensorBlockEntity be)
        {
            return be.getConfig().getInputPorts();
        }
        return NO_PORTS;
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {
        if (world.getBlockEntity(pos) instanceof CoreSensorBlockEntity be)
        {
            be.networkChanged();
        }
    }

    @Override
    public ReceiverOrganismComponent get(World world, BlockPos pos, BlockState state)
    {
        return (ReceiverOrganismComponent) world.getBlockEntity(pos);
    }
}
