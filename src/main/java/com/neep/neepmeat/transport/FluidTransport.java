package com.neep.neepmeat.transport;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.item.TankItem;
import com.neep.neepmeat.transport.block.fluid_transport.*;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidTransport
{
    // --- Fluid Pipes ---
    public static Block PIPE = BlockRegistry.queue(new FluidPipeBlock("fluid_pipe", NMBlocks.block().factory(FluidComponentItem::new) , NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block FILTER_PIPE = BlockRegistry.queue(new FilterPipeBlock("filter_pipe", NMBlocks.block().tooltip(TooltipSupplier.simple(2)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block STOP_VALVE = BlockRegistry.queue(new StopValveBlock("stop_valve", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block CHECK_VALVE = BlockRegistry.queue(new CheckValveBlock("check_valve", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block LIMITER_VALVE = BlockRegistry.queue(new LimiterValveBlock("limiter_valve", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block WINDOW_PIPE = BlockRegistry.queue(new WindowPipeBlock("window_fluid_pipe", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block COPPER_PIPE = BlockRegistry.queue(new CapillaryFluidPipeBlock("copper_pipe", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block PUMP = BlockRegistry.queue(new PumpBlock("pump", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block TANK = BlockRegistry.queue(new TankBlock("basic_tank", NMBlocks.block().factory(TankItem::new), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block GLASS_TANK = BlockRegistry.queue(new GlassTankBlock("basic_glass_tank", NMBlocks.block().factory(TankItem::new), NMBlocks.FLUID_MACHINE_SETTINGS));

    public static long MAX_TRANSFER = FluidConstants.BUCKET / 8;

    public static void init()
    {
        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, PIPE);
        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, COPPER_PIPE);
        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, STOP_VALVE);
        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, FILTER_PIPE);
        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, CHECK_VALVE);
        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, WINDOW_PIPE);

        PipeNetwork.registerEvent();
    }

    public static PipeNetwork getNetwork(World world, BlockPos pos, BlockState state, BlockEntity be, Void context)
    {
        if (be instanceof FluidPipeBlockEntity pipeBE)
        {
            return pipeBE.getPipeVertex().getNetwork();
        }
        return null;
    }
}