package com.neep.neepmeat.transport;

import com.google.common.collect.Maps;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.Ignore;
import com.neep.meatlib.registry.annotation.Path;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepmeat.NeepMeat;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegisterMe(NeepMeat.NAMESPACE)
public class FluidTransport
{
    public static final RegistrationContext C = new RegistrationContext(NeepMeat.NAMESPACE);

    public static long MAX_TRANSFER = FluidConstants.BUCKET / 8;

    public static final Map<FluidPipe.PipeCol, FluidPipeBlock> COLOURED_FLUID_PIPES = Maps.newEnumMap(FluidPipe.PipeCol.class);

    // --- Fluid Pipes ---
    public static Block FLUID_PIPE = new FluidPipeBlock(C, FluidPipe.PipeCol.ANY, NMBlocks.block().factory(FluidComponentItem::new), NMBlocks.FLUID_PIPE_SETTINGS);
    public static Block ENCASED_FLUID_PIPE = new EncasedFluidPipeBlock(C, FluidPipe.PipeCol.ANY, NMBlocks.block().factory(FluidComponentItem::new).tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS);

    @Ignore public static Block WHITE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.WHITE);
    @Ignore public static Block ORANGE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.ORANGE);
    @Ignore public static Block MAGENTA_FLUID_PIPE = makePipe(FluidPipe.PipeCol.MAGENTA);
    @Ignore public static Block LIGHT_BLUE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.LIGHT_BLUE);
    @Ignore public static Block YELLOW_FLUID_PIPE = makePipe(FluidPipe.PipeCol.YELLOW);
    @Ignore public static Block LIME_FLUID_PIPE = makePipe(FluidPipe.PipeCol.LIME);
    @Ignore public static Block PINK_FLUID_PIPE = makePipe(FluidPipe.PipeCol.PINK);
    @Ignore public static Block GRAY_FLUID_PIPE = makePipe(FluidPipe.PipeCol.GRAY);
    @Ignore public static Block LIGHT_GRAY_FLUID_PIPE = makePipe(FluidPipe.PipeCol.LIGHT_GRAY);
    @Ignore public static Block CYAN_FLUID_PIPE = makePipe(FluidPipe.PipeCol.CYAN);
    @Ignore public static Block PURPLE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.PURPLE);
    @Ignore public static Block BLUE_FLUID_PIPE = makePipe(FluidPipe.PipeCol.BLUE);
    @Ignore public static Block BROWN_FLUID_PIPE = makePipe(FluidPipe.PipeCol.BROWN);
    @Ignore public static Block GREEN_FLUID_PIPE = makePipe(FluidPipe.PipeCol.GREEN);
    @Ignore public static Block RED_FLUID_PIPE = makePipe(FluidPipe.PipeCol.RED);
    @Ignore public static Block BLACK_FLUID_PIPE = makePipe(FluidPipe.PipeCol.BLACK);

    public static Block FILTER_PIPE = new FilterPipeBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(2)), NMBlocks.FLUID_PIPE_SETTINGS);
    public static Block STOP_VALVE = new StopValveBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS);
    public static Block CHECK_VALVE = new CheckValveBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS);
    public static Block LIMITER_VALVE = new LimiterValveBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS);
    @Path("window_fluid_pipe") public static Block WINDOW_PIPE = new WindowPipeBlock(C, NMBlocks.block().tooltip(TooltipSupplier.blank()), NMBlocks.FLUID_PIPE_SETTINGS);
    public static Block COPPER_PIPE = new CapillaryFluidPipeBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_PIPE_SETTINGS);
    @Path("pump") public static Block PUMP = new PumpBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS);

    public static Block BASIC_TANK = new TankBlock(C, NMBlocks.block().factory(TankItem::new).tooltip(TooltipSupplier.hidden(2)), NMBlocks.FLUID_MACHINE_SETTINGS);
    public static Block BASIC_GLASS_TANK = new GlassTankBlock(C, NMBlocks.block().factory(TankItem::new).tooltip(TooltipSupplier.hidden(2)), NMBlocks.FLUID_MACHINE_SETTINGS);
    public static Block ADVANCED_TANK = new AdvancedTankBlock(C, NMBlocks.block().factory(TankItem::new).tooltip(TooltipSupplier.hidden(2)), NMBlocks.FLUID_MACHINE_SETTINGS);

    public static Block MULTI_TANK = new MultiTankBlock(C, NMBlocks.block(), NMBlocks.FLUID_MACHINE_SETTINGS);
    public static Block FLUID_BUFFER = new FluidBufferBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS);
    public static Block FLUID_INTERFACE = new FluidInterfaceBlock(C, NMBlocks.block().tooltip((TooltipSupplier.simple(1))).factory(FluidComponentItem::new), NMBlocks.FLUID_MACHINE_SETTINGS);
    public static Block FLUID_DRAIN = new FluidDrainBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS);

    public static FlexTankBlock FLEX_TANK = new FlexTankBlock(C, 8 * FluidConstants.BUCKET, () -> NMBlockEntities.FLEX_TANK,
            NMBlocks.block().tooltip(TooltipSupplier.simple(2)), NMBlocks.FLUID_MACHINE_SETTINGS);
    public static FlexTankBlock ADVANCED_FLEX_TANK = new FlexTankBlock(C, 16 * FluidConstants.BUCKET, () -> NMBlockEntities.ADVANCED_FLEX_TANK,
            NMBlocks.block().tooltip(TooltipSupplier.simple(2)), NMBlocks.FLUID_MACHINE_SETTINGS);

    public static Block FLUID_GAUGE = new FluidGaugeBlock<>(C, () -> NMBlockEntities.FLUID_GAUGE,
            NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS);
    public static Block ITEM_GAUGE = new FluidGaugeBlock<>(C, () -> NMBlockEntities.ITEM_GAUGE,
            NMBlocks.block().tooltip(TooltipSupplier.simple(1)), NMBlocks.FLUID_MACHINE_SETTINGS);

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
        var pipe = C.addParent(new Identifier(NeepMeat.NAMESPACE, "fluid_pipe_" + col.name().toLowerCase()), new FluidPipeBlock(C, col, NMBlocks.block().factory(FluidComponentItem::new), NMBlocks.FLUID_PIPE_SETTINGS));
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