package com.neep.neepmeat.transport;

import com.google.common.collect.Maps;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.Registries.BLOCKRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.item.TankItem;
import com.neep.neepmeat.machine.multitank.MultiTankBlock;
import com.neep.neepmeat.transport.api.pipe.FluidPipe;
import com.neep.neepmeat.transport.block.fluid_transport.*;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FluidTransport
{
    public static long MAX_TRANSFER = FluidConstants.BUCKET / 8;

    public static final Map<FluidPipe.PipeCol, FluidPipeBlock> COLOURED_FLUID_PIPES = Maps.newEnumMap(FluidPipe.PipeCol.class);

    // --- Fluid Pipes ---
    public static Block FLUID_PIPE = BlockRegistry.queue(new FluidPipeBlock("fluid_pipe", FluidPipe.PipeCol.ANY, NMBlocks.block().factory(FluidComponentItem::new), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block ENCASED_FLUID_PIPE = BlockRegistry.queue(new EncasedFluidPipeBlock("encased_fluid_pipe", FluidPipe.PipeCol.ANY, NMBlocks.block().factory(FluidComponentItem::new).tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));

    public static Block WHITE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.WHITE);
    public static Block ORANGE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.ORANGE);
    public static Block MAGENTA_FLUID_PIPE = makePipe(FluidPipe.PipeCol.MAGENTA);
    public static Block LIGHT_BLUE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.LIGHT_BLUE);
    public static Block YELLOW_FLUID_PIPE = makePipe(FluidPipe.PipeCol.YELLOW);
    public static Block LIME_FLUID_PIPE = makePipe(FluidPipe.PipeCol.LIME);
    public static Block PINK_FLUID_PIPE = makePipe(FluidPipe.PipeCol.PINK);
    public static Block GRAY_FLUID_PIPE = makePipe(FluidPipe.PipeCol.GRAY);
    public static Block LIGHT_GRAY_FLUID_PIPE = makePipe(FluidPipe.PipeCol.LIGHT_GRAY);
    public static Block CYAN_FLUID_PIPE = makePipe(FluidPipe.PipeCol.CYAN);
    public static Block PURPLE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.PURPLE);
    public static Block BLUE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.BLUE);
    public static Block BROWN_FLUID_PIPE = makePipe(FluidPipe.PipeCol.BROWN);
    public static Block GREEN_FLUID_PIPE = makePipe(FluidPipe.PipeCol.GREEN);
    public static Block RED_FLUID_PIPE = makePipe(FluidPipe.PipeCol.RED);
    public static Block BLACK_FLUID_PIPE = makePipe(FluidPipe.PipeCol.BLACK);

    public static Block FILTER_PIPE = BlockRegistry.queue(new FilterPipeBlock("filter_pipe", NMBlocks.block().tooltip(TooltipSupplier.simple(2)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block STOP_VALVE = BlockRegistry.queue(new StopValveBlock("stop_valve", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block CHECK_VALVE = BlockRegistry.queue(new CheckValveBlock("check_valve", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block LIMITER_VALVE = BlockRegistry.queue(new LimiterValveBlock("limiter_valve", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block WINDOW_PIPE = BlockRegistry.queue(new WindowPipeBlock("window_fluid_pipe", NMBlocks.block().tooltip(TooltipSupplier.blank()), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block COPPER_PIPE = BlockRegistry.queue(new CapillaryFluidPipeBlock("copper_pipe", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS));
    public static Block PUMP = BlockRegistry.queue(new PumpBlock("pump", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS));

    public static Block BASIC_TANK = BlockRegistry.queue(new TankBlock("basic_tank", NMBlocks.block().factory(TankItem::new).tooltip(TooltipSupplier.hidden(2)), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block BASIC_GLASS_TANK = BlockRegistry.queue(new GlassTankBlock("basic_glass_tank", NMBlocks.block().factory(TankItem::new).tooltip(TooltipSupplier.hidden(2)), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block ADVANCED_TANK = BlockRegistry.queue(new AdvancedTankBlock("advanced_tank", NMBlocks.block().factory(TankItem::new).tooltip(TooltipSupplier.hidden(2)), NMBlocks.FLUID_MACHINE_SETTINGS));

    public static Block MULTI_TANK = BlockRegistry.queue(new MultiTankBlock("multi_tank", NMBlocks.block(), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block FLUID_BUFFER = BlockRegistry.queue(new FluidBufferBlock("fluid_buffer", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block FLUID_INTERFACE = BlockRegistry.queue(new FluidInterfaceBlock("fluid_interface", NMBlocks.block().tooltip((TooltipSupplier.simple(1))).factory(FluidComponentItem::new), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block FLUID_DRAIN = BlockRegistry.queue(new FluidDrainBlock("fluid_drain", NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS));

    public static FlexTankBlock FLEX_TANK = BlockRegistry.queue(new FlexTankBlock("flex_tank", 8 * FluidConstants.BUCKET, () -> NMBlockEntities.FLEX_TANK,
            NMBlocks.block().tooltip(TooltipSupplier.simple(2)), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static FlexTankBlock ADVANCED_FLEX_TANK = BlockRegistry.queue(new FlexTankBlock("advanced_flex_tank", 16 * FluidConstants.BUCKET, () -> NMBlockEntities.ADVANCED_FLEX_TANK,
            NMBlocks.block().tooltip(TooltipSupplier.simple(2)), NMBlocks.FLUID_MACHINE_SETTINGS));

    public static Block FLUID_GAUGE = BlockRegistry.queue(new FluidGaugeBlock<>("fluid_gauge", () -> NMBlockEntities.FLUID_GAUGE,
            NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS));
    public static Block ITEM_GAUGE = BlockRegistry.queue(new FluidGaugeBlock<>("item_gauge", () -> NMBlockEntities.ITEM_GAUGE,
            NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS));

    public static void init()
    {
//        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, PIPE);
//        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, COPPER_PIPE);
//        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, STOP_VALVE);
//        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, FILTER_PIPE);
//        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, CHECK_VALVE);
//        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, LIMITER_VALVE);
//        PipeNetwork.LOOKUP.registerForBlocks(FluidTransport::getNetwork, WINDOW_PIPE);

//        PipeVertex.LOOKUP.registerForBlocks(FluidTransport::getVertex, PIPE);
        PipeVertex.LOOKUP.registerFallback(FluidTransport::getVertex);
    }

    private static FluidPipeBlock makePipe(FluidPipe.PipeCol col)
    {
        var pipe = BlockRegistry.queue(new FluidPipeBlock("fluid_pipe_" + col.name().toLowerCase(), col, NMBlocks.block().factory(FluidComponentItem::new), NMBlocks.FLUID_PIPE_SETTINGS));
        COLOURED_FLUID_PIPES.put(col, pipe);
        return pipe;
    }

    private static PipeVertex getVertex(World world, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, Void unused)
    {
        if (blockEntity instanceof FluidPipeBlockEntity<?> be)
        {
            return be.getPipeVertex();
        }
        return null;
    }
}