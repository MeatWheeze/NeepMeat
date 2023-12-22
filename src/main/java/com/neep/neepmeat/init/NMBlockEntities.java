package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.multiblock.IMultiBlock;
import com.neep.neepmeat.block.vat.FluidPortBlock;
import com.neep.neepmeat.block.vat.ItemPortBlock;
import com.neep.neepmeat.block.entity.*;
import com.neep.neepmeat.machine.assembler.AssemblerBlock;
import com.neep.neepmeat.machine.assembler.AssemblerBlockEntity;
import com.neep.neepmeat.machine.breaker.LinearOscillatorBlockEntity;
import com.neep.neepmeat.machine.crafting_station.WorkstationBlockEntity;
import com.neep.neepmeat.machine.death_blades.DeathBladesBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.entity.CheckValveBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FluidDrainBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.entity.StopValveBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.*;
import com.neep.neepmeat.transport.machine.fluid.*;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.block.entity.machine.*;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.machine.alloy_kiln.AlloyKilnBlockEntity;
import com.neep.neepmeat.machine.casting_basin.CastingBasinBlockEntity;
import com.neep.neepmeat.machine.content_detector.ContentDetectorBlockEntity;
import com.neep.neepmeat.machine.converter.ConverterBlockEntity;
import com.neep.neepmeat.machine.deployer.DeployerBlockEntity;
import com.neep.neepmeat.machine.heater.HeaterBlockEntity;
import com.neep.neepmeat.machine.pedestal.PedestalBlockEntity;
import com.neep.neepmeat.machine.crucible.AlembicBlockEntity;
import com.neep.neepmeat.machine.crucible.CrucibleBlockEntity;
import com.neep.neepmeat.machine.dumper.DumperBlockEntity;
import com.neep.neepmeat.machine.grinder.GrinderBlockEntity;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressBlockEntity;
import com.neep.neepmeat.machine.mixer.MixerBlockEntity;
import com.neep.neepmeat.machine.mixer.MixerTopBlockEntity;
import com.neep.neepmeat.machine.motor.MotorBlockEntity;
import com.neep.neepmeat.machine.multitank.MultiTankBlockEntity;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlock;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlockEntity;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineBlockEntity;
import com.neep.neepmeat.machine.transducer.TransducerBlockEntity;
import com.neep.neepmeat.machine.trommel.TrommelBlockEntity;
import com.neep.neepmeat.machine.trommel.TrommelStructureBlockEntity;
import com.neep.neepmeat.transport.machine.item.BufferBlockEntity;
import com.neep.neepmeat.transport.machine.item.EjectorBlockEntity;
import com.neep.neepmeat.transport.machine.item.ItemPumpBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMBlockEntities
{
    public static BlockEntityType<?> NODE_BLOCK_ENTITY;
    public static BlockEntityType<PumpBlockEntity> PUMP;
    public static BlockEntityType<TankBlockEntity> TANK_BLOCK_ENTITY;
    public static BlockEntityType<FluidBufferBlockEntity> FLUID_BUFFER;
    public static BlockEntityType<CheckValveBlockEntity> CHECK_VALVE;
    public static BlockEntityType<StopValveBlockEntity> STOP_VALVE;

    public static BlockEntityType<DisplayPlatformBlockEntity> ITEM_BUFFER_BLOCK_ENTITY;
    public static BlockEntityType<ContentDetectorBlockEntity> CONTENT_DETECTOR;
    public static BlockEntityType<GlassTankBlockEntity> GLASS_TANK_BLOCK_ENTITY;
    public static BlockEntityType<MultiTankBlockEntity> MULTI_TANK;
    public static BlockEntityType<ItemDuctBlockEntity> ITEM_DUCT_BLOCK_ENTITY;
    public static BlockEntityType<TrommelBlockEntity> TROMMEL;
    public static BlockEntityType<MotorBlockEntity> MOTOR;
    public static BlockEntityType<LinearOscillatorBlockEntity> LINEAR_OSCILLATOR;
    public static BlockEntityType<DeployerBlockEntity> DEPLOYER;
    public static BlockEntityType<AgitatorBlockEntity> AGITATOR;
    public static BlockEntityType<ItemPortBlock.BlockEntity> VAT_ITEM_PORT;
    public static BlockEntityType<FluidPortBlock.BlockEntity> VAT_FLUID_PORT;
    public static BlockEntityType<IMultiBlock.Entity> VAT_CASING;
    public static BlockEntityType<IMultiBlock.Entity> VAT_WINDOW;
    public static BlockEntityType<VatControllerBlockEntity> VAT_CONTROLLER;

    public static BlockEntityType<ConverterBlockEntity> CONVERTER;
    public static BlockEntityType<ConverterBlockEntity> LARGE_CONVERTER;

    public static BlockEntityType<FluidDrainBlockEntity> FLUID_DRAIN;
    public static BlockEntityType<FluidInterfaceBlockEntity> FLUID_INTERFACE;
    public static BlockEntityType<IntegratorBlockEntity> INTEGRATOR;
    public static BlockEntityType<HeaterBlockEntity> HEATER;
    public static BlockEntityType<SpigotBlockEntity> SPIGOT;

    public static BlockEntityType<BigLeverBlockEntity> BIG_LEVER;

    public static BlockEntityType<ItemPipeBlockEntity> PNEUMATIC_PIPE;
    public static BlockEntityType<MergePipeBlockEntity> MERGE_ITEM_PIPE;
    public static BlockEntityType<BufferBlockEntity> BUFFER;
    public static BlockEntityType<ItemPumpBlockEntity> ITEM_PUMP;
    public static BlockEntityType<EjectorBlockEntity> EJECTOR;
    public static BlockEntityType<RouterBlockEntity> ROUTER;

    public static BlockEntityType<MixerBlockEntity> MIXER;
    public static BlockEntityType<MixerTopBlockEntity> MIXER_TOP;
    public static BlockEntityType<GrinderBlockEntity> GRINDER;
    public static BlockEntityType<StirlingEngineBlockEntity> STIRLING_ENGINE;
    public static BlockEntityType<AlloyKilnBlockEntity> ALLOY_KILN;

    public static BlockEntityType<CrucibleBlockEntity> CRUCIBLE;
    public static BlockEntityType<AlembicBlockEntity> ALEMBIC;
    public static BlockEntityType<TransducerBlockEntity> TRANSDUCER;
    public static BlockEntityType<DumperBlockEntity> DUMPER;
    public static BlockEntityType<TrommelStructureBlockEntity> TROMMEL_STRUCTURE;
    public static BlockEntityType<SmallTrommelBlockEntity> SMALL_TROMMEL;
    public static BlockEntityType<SmallTrommelBlockEntity.Structure> SMALL_TROMMEL_STRUCTURE;
    public static BlockEntityType<CastingBasinBlockEntity> CASTING_BASIN;
    public static BlockEntityType<HydraulicPressBlockEntity> HYDRAULIC_PRESS;
    public static BlockEntityType<PedestalBlockEntity> PEDESTAL;
    public static BlockEntityType<AssemblerBlockEntity> ASSEMBLER;
    public static BlockEntityType<WorkstationBlockEntity> WORKSTATION;
    public static BlockEntityType<DeathBladesBlockEntity> DEATH_BLADES;

    public static <T extends net.minecraft.block.entity.BlockEntity> BlockEntityType<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block)
    {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, id),
                FabricBlockEntityTypeBuilder.create(factory, block).build());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void initialise()
    {
        // --- Fluid Transfer ---
        PUMP = registerBlockEntity("pump_block_entity", PumpBlockEntity::new, NMBlocks.PUMP);
        FluidPump.SIDED.registerForBlockEntity(PumpBlockEntity::getPump, PUMP);

        TANK_BLOCK_ENTITY = registerBlockEntity("tank_block_entity", TankBlockEntity::new, NMBlocks.TANK);
        FluidStorage.SIDED.registerForBlockEntity(TankBlockEntity::getStorage, NMBlockEntities.TANK_BLOCK_ENTITY);

        MULTI_TANK = registerBlockEntity("multi_tank", MultiTankBlockEntity::new, NMBlocks.MULTI_TANK);
        FLUID_BUFFER = registerBlockEntity("fluid_buffer", FluidBufferBlockEntity::new, NMBlocks.FLUID_BUFFER);
        GLASS_TANK_BLOCK_ENTITY = registerBlockEntity("glass_tank_block_entity", GlassTankBlockEntity::new, NMBlocks.GLASS_TANK);
        FluidStorage.SIDED.registerForBlockEntity(GlassTankBlockEntity::getStorage, NMBlockEntities.GLASS_TANK_BLOCK_ENTITY);
        NODE_BLOCK_ENTITY = registerBlockEntity("node_storage", NodeContainerBlockEntity::new, NMBlocks.PIPE);
        STOP_VALVE = registerBlockEntity("stop_valve", StopValveBlockEntity::new, NMBlocks.STOP_VALVE);

        FLUID_DRAIN = registerBlockEntity("fluid_drain", FluidDrainBlockEntity::new, NMBlocks.FLUID_DRAIN);
        FLUID_INTERFACE = registerBlockEntity("fluid_port", FluidInterfaceBlockEntity::new, NMBlocks.FLUID_INTERFACE);
        HEATER = registerBlockEntity("heater", HeaterBlockEntity::new, NMBlocks.HEATER);
//        SPIGOT = registerBlockEntity("spigot", SpigotBlockEntity::new, NMBlocks.SPIGOT);

        // --- Item Transfer ---
        ITEM_DUCT_BLOCK_ENTITY = registerBlockEntity("item_duct", ItemDuctBlockEntity::new, NMBlocks.ITEM_DUCT);
        ITEM_BUFFER_BLOCK_ENTITY = registerBlockEntity("item_buffer", DisplayPlatformBlockEntity::new, NMBlocks.ITEM_BUFFER);
        PNEUMATIC_PIPE = registerBlockEntity("pneumatic_pipe", ItemPipeBlockEntity::new, NMBlocks.PNEUMATIC_TUBE);
        MERGE_ITEM_PIPE = registerBlockEntity("merge_item_pipe", MergePipeBlockEntity::new, NMBlocks.MERGE_ITEM_PIPE);
        BUFFER = registerBlockEntity("buffer", BufferBlockEntity::new, NMBlocks.BUFFER);
        CONTENT_DETECTOR = registerBlockEntity("content_detector", ContentDetectorBlockEntity::new, NMBlocks.CONTENT_DETECTOR);
        EJECTOR = registerBlockEntity("ejector", EjectorBlockEntity::new, NMBlocks.EJECTOR);
        ITEM_PUMP = registerBlockEntity("item_pump", ItemPumpBlockEntity::new, NMBlocks.ITEM_PUMP);
        ROUTER = registerBlockEntity("router", RouterBlockEntity::new, NMBlocks.ROUTER);
        DUMPER = registerBlockEntity("dumper", DumperBlockEntity::new, NMBlocks.DUMPER);
        ItemStorage.SIDED.registerForBlockEntity(DumperBlockEntity::getStorage, DUMPER);

        // --- Machines ---
        INTEGRATOR = registerBlockEntity("integrator_egg", IntegratorBlockEntity::new, NMBlocks.INTEGRATOR_EGG);
        ItemStorage.SIDED.registerForBlockEntity(IntegratorBlockEntity::getItemStorage, INTEGRATOR);
        TROMMEL = registerBlockEntity("trommel", TrommelBlockEntity::new, NMBlocks.TROMMEL);
        TROMMEL_STRUCTURE = registerBlockEntity("trommel_structure", TrommelStructureBlockEntity::new, NMBlocks.TROMMEL_STRUCTURE);
        SMALL_TROMMEL = registerBlockEntity("small_trommel", SmallTrommelBlockEntity::new, NMBlocks.SMALL_TROMMEL);
        SMALL_TROMMEL_STRUCTURE = registerBlockEntity("small_trommel_structure", SmallTrommelBlockEntity.Structure::new, NMBlocks.SMALL_TROMMEL_STRUCTURE);
        FluidStorage.SIDED.registerForBlockEntity(SmallTrommelBlockEntity::getInputStorage, SMALL_TROMMEL);
        FluidStorage.SIDED.registerForBlocks(SmallTrommelBlock.Structure::getFluidStorage, NMBlocks.SMALL_TROMMEL_STRUCTURE);
        ItemStorage.SIDED.registerForBlocks(SmallTrommelBlock.Structure::getItemStorage, NMBlocks.SMALL_TROMMEL_STRUCTURE);

        BIG_LEVER = registerBlockEntity("big_lever", BigLeverBlockEntity::new, NMBlocks.BIG_LEVER);
        MOTOR = registerBlockEntity("motor_unit", MotorBlockEntity::new, NMBlocks.MOTOR);
        FluidPump.SIDED.registerForBlockEntity(BloodMachineBlockEntity::getPump, MOTOR);
        STIRLING_ENGINE = registerBlockEntity("stirling_engine", StirlingEngineBlockEntity::new, NMBlocks.STIRLING_ENGINE);
        LINEAR_OSCILLATOR = registerBlockEntity("linear_oscillator", LinearOscillatorBlockEntity::new, NMBlocks.LINEAR_OSCILLATOR);
        DEPLOYER = registerBlockEntity("deployer", DeployerBlockEntity::new, NMBlocks.DEPLOYER);
        AGITATOR = registerBlockEntity("agitator", AgitatorBlockEntity::new, NMBlocks.AGITATOR);

        GRINDER = registerBlockEntity("grinder", GrinderBlockEntity::new, NMBlocks.GRINDER);
        ALLOY_KILN = registerBlockEntity("alloy_kiln", AlloyKilnBlockEntity::new, NMBlocks.ALLOY_KILN);
        CRUCIBLE = registerBlockEntity("crucible", CrucibleBlockEntity::new, NMBlocks.CRUCIBLE);
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getStorage(direction), CRUCIBLE);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getItemStorage(direction), CRUCIBLE);
        ALEMBIC = registerBlockEntity("collector", AlembicBlockEntity::new, NMBlocks.ALEMBIC);
        FluidStorage.SIDED.registerForBlockEntity(AlembicBlockEntity::getStorage, ALEMBIC);

        CASTING_BASIN = registerBlockEntity("casting_basin", CastingBasinBlockEntity::new, NMBlocks.CASTING_BASIN);
        FluidStorage.SIDED.registerForBlockEntity((be, dir) -> be.getStorage().fluid(dir), CASTING_BASIN);
        ItemStorage.SIDED.registerForBlockEntity((be, dir) -> be.getStorage().item(dir), CASTING_BASIN);

        HYDRAULIC_PRESS = registerBlockEntity("hydraulic_press", HydraulicPressBlockEntity::new, NMBlocks.HYDRAULIC_PRESS);
        FluidStorage.SIDED.registerForBlockEntity(HydraulicPressBlockEntity::getStorage, HYDRAULIC_PRESS);

        VAT_WINDOW = registerBlockEntity("vat_window", (pos, state) -> new IMultiBlock.Entity(VAT_WINDOW, pos, state), NMBlocks.VAT_WINDOW);
        VAT_CASING = registerBlockEntity("vat_casing", (pos, state) -> new IMultiBlock.Entity(VAT_CASING, pos, state), NMBlocks.VAT_CASING);
        VAT_ITEM_PORT = registerBlockEntity("vat_item_port", ItemPortBlock.BlockEntity::new, NMBlocks.VAT_ITEM_PORT);
        VAT_FLUID_PORT = registerBlockEntity("vat_fluid_port", FluidPortBlock.BlockEntity::new, NMBlocks.VAT_FLUID_PORT);
        VAT_CONTROLLER = registerBlockEntity("vat_controller", VatControllerBlockEntity::new, NMBlocks.VAT_CONTROLLER);

        CONVERTER = registerBlockEntity("converter", ConverterBlockEntity::new, NMBlocks.CONVERTER);

        MIXER = registerBlockEntity("mixer", MixerBlockEntity::new, NMBlocks.MIXER);
        MIXER_TOP = registerBlockEntity("mixer_top", MixerTopBlockEntity::new, NMBlocks.MIXER_TOP);

        TRANSDUCER = registerBlockEntity("transducer", TransducerBlockEntity::new, NMBlocks.TRANSDUCER);
        FluidStorage.SIDED.registerForBlockEntity(TransducerBlockEntity::getStorage, TRANSDUCER);
        FluidPump.SIDED.registerForBlockEntity(TransducerBlockEntity::getPump, TRANSDUCER);

        PEDESTAL = registerBlockEntity("pedestal", PedestalBlockEntity::new, NMBlocks.PEDESTAL);
        ItemStorage.SIDED.registerForBlockEntity(PedestalBlockEntity::getStorage, PEDESTAL);

        ASSEMBLER = registerBlockEntity("assembler", AssemblerBlockEntity::new, NMBlocks.ASSEMBLER);
        ItemStorage.SIDED.registerForBlockEntity((be, dir) -> be.getStorage().getStorage(dir, false), ASSEMBLER);
        FluidStorage.SIDED.registerForBlocks(AssemblerBlock.Top::getStorage, NMBlocks.ASSEMBLER_TOP);

        WORKSTATION = registerBlockEntity("workstation", WorkstationBlockEntity::new, NMBlocks.WORKSTATION);

        DEATH_BLADES = registerBlockEntity("death_blades", DeathBladesBlockEntity::new, NMBlocks.DEATH_BLADES);

        ItemStorage.SIDED.registerSelf(BUFFER);
        FluidStorage.SIDED.registerSelf(FLUID_INTERFACE);
        ItemStorage.SIDED.registerSelf(DEPLOYER);
        ItemStorage.SIDED.registerSelf(ITEM_DUCT_BLOCK_ENTITY);

        ItemStorage.SIDED.registerSelf(VAT_ITEM_PORT);
        FluidStorage.SIDED.registerSelf(VAT_FLUID_PORT);


        FluidStorage.SIDED.registerForBlockEntity(PumpBlockEntity::getBuffer, PUMP);

        FluidStorage.SIDED.registerForBlockEntity(MixerBlockEntity::getFluidStorage, MIXER);
        ItemStorage.SIDED.registerForBlockEntity(MixerBlockEntity::getItemStorage, MIXER);
        FluidStorage.SIDED.registerForBlocks(MixerTopBlockEntity::getBottomStorage, NMBlocks.MIXER_TOP);


        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getItemStorage(direction), GRINDER);

        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getStorage(direction), ALLOY_KILN);
        ItemStorage.SIDED.registerForBlockEntity(DisplayPlatformBlockEntity::getStorage, ITEM_BUFFER_BLOCK_ENTITY);

        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getFuelStorage(direction), STIRLING_ENGINE);

        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage(), MULTI_TANK);

        FluidStorage.SIDED.registerFallback((world, pos, state, be, direction) ->
        {
            if (be instanceof FluidBuffer.FluidBufferProvider provider)
            {
                return provider.getBuffer(direction);
            }
            return null;
        });
    }
}
