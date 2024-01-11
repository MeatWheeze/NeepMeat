package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.DataPort;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.multiblock.MultiBlock;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.block.HoldingTrackBlock;
import com.neep.neepmeat.block.entity.*;
import com.neep.neepmeat.block.entity.machine.AgitatorBlockEntity;
import com.neep.neepmeat.block.entity.machine.VatControllerBlockEntity;
import com.neep.neepmeat.block.vat.FluidPortBlock;
import com.neep.neepmeat.block.vat.ItemPortBlock;
import com.neep.neepmeat.machine.Heatable;
import com.neep.neepmeat.machine.HeatableFurnace;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorBlockEntity;
import com.neep.neepmeat.machine.advanced_motor.AdvancedMotorBlockEntity;
import com.neep.neepmeat.machine.alloy_kiln.AlloyKilnBlockEntity;
import com.neep.neepmeat.machine.assembler.AssemblerBlockEntity;
import com.neep.neepmeat.machine.bottler.BottlerBlockEntity;
import com.neep.neepmeat.machine.breaker.LinearOscillatorBlockEntity;
import com.neep.neepmeat.machine.casting_basin.CastingBasinBlockEntity;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpBlockEntity;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpStructure;
import com.neep.neepmeat.machine.charnel_pump.WrithingEarthSpoutBlockEntity;
import com.neep.neepmeat.machine.content_detector.InventoryDetectorBlockEntity;
import com.neep.neepmeat.machine.converter.ConverterBlockEntity;
import com.neep.neepmeat.machine.crafting_station.WorkstationBlockEntity;
import com.neep.neepmeat.machine.crucible.AlembicBlockEntity;
import com.neep.neepmeat.machine.crucible.CrucibleBlockEntity;
import com.neep.neepmeat.machine.death_blades.DeathBladesBlockEntity;
import com.neep.neepmeat.machine.deployer.DeployerBlockEntity;
import com.neep.neepmeat.machine.dumper.DumperBlockEntity;
import com.neep.neepmeat.machine.fluid_exciter.FluidExciterBlockEntity;
import com.neep.neepmeat.machine.fluid_rationer.FluidRationerBlockEntity;
import com.neep.neepmeat.machine.flywheel.FlywheelBlockEntity;
import com.neep.neepmeat.machine.grinder.GrinderBlockEntity;
import com.neep.neepmeat.machine.heater.HeaterBlockEntity;
import com.neep.neepmeat.machine.homogeniser.HomogeniserBlockEntity;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressBlockEntity;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.machine.item_mincer.ItemMincerBlockEntity;
import com.neep.neepmeat.machine.large_motor.LargeMotorBlockEntity;
import com.neep.neepmeat.machine.large_motor.LargeMotorStructureEntity;
import com.neep.neepmeat.machine.mincer.MincerBlockEnity;
import com.neep.neepmeat.machine.mixer.MixerBlockEntity;
import com.neep.neepmeat.machine.motor.LiquidFuelMachine;
import com.neep.neepmeat.machine.motor.MotorBlockEntity;
import com.neep.neepmeat.machine.multitank.MultiTankBlockEntity;
import com.neep.neepmeat.machine.pedestal.PedestalBlockEntity;
import com.neep.neepmeat.machine.pylon.PylonBlockEntity;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlock;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlockEntity;
import com.neep.neepmeat.machine.solidity_detector.SolidityDetectorBlockEntity;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineBlockEntity;
import com.neep.neepmeat.machine.surgery_platform.SurgeryPlatformBlockEntity;
import com.neep.neepmeat.machine.synthesiser.SynthesiserBlockEntity;
import com.neep.neepmeat.machine.synthesiser.SynthesiserStorage;
import com.neep.neepmeat.machine.transducer.TransducerBlockEntity;
import com.neep.neepmeat.machine.trommel.TrommelBlockEntity;
import com.neep.neepmeat.machine.trommel.TrommelStructureBlockEntity;
import com.neep.neepmeat.machine.trough.TroughBlockEntity;
import com.neep.neepmeat.machine.upgrade_manager.UpgradeManagerBlockEntity;
import com.neep.neepmeat.machine.well_head.WellHeadBlockEntity;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.component.TableComponent;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.block.energy_transport.entity.VSCBlockEntity;
import com.neep.neepmeat.transport.block.energy_transport.entity.VascularConduitBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.CheckValveBlock;
import com.neep.neepmeat.transport.block.fluid_transport.StopValveBlock;
import com.neep.neepmeat.transport.block.fluid_transport.entity.*;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemDuctBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemPipeBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.MergePipeBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.RouterBlockEntity;
import com.neep.neepmeat.transport.fluid_network.node.BlockPipeVertex;
import com.neep.neepmeat.transport.machine.fluid.*;
import com.neep.neepmeat.transport.machine.item.BufferBlockEntity;
import com.neep.neepmeat.transport.machine.item.EjectorBlockEntity;
import com.neep.neepmeat.transport.machine.item.ItemPumpBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMBlockEntities
{
    public static BlockEntityType<FluidPipeBlockEntity<?>> FLUID_PIPE;
    public static BlockEntityType<?> CHECK_VALVE;
    public static BlockEntityType<?> STOP_VALVE;
    public static BlockEntityType<FilterPipeBlockEntity> FILTER_PIPE;
    public static BlockEntityType<LimiterValveBlockEntity> LIMITER_VALVE;
    public static BlockEntityType<WindowPipeBlockEntity> WINDOW_PIPE;
    public static BlockEntityType<PumpBlockEntity> PUMP;
    public static BlockEntityType<TankBlockEntity> TANK;
    public static BlockEntityType<TankBlockEntity> ADVANCED_TANK;
    public static BlockEntityType<FluidBufferBlockEntity> FLUID_BUFFER;
    public static BlockEntityType<FlexTankBlockEntity> FLEX_TANK;
    public static BlockEntityType<FlexTankBlockEntity> ADVANCED_FLEX_TANK;
    public static BlockEntityType<FluidGaugeBlockEntity<FluidVariant>> FLUID_GAUGE;
    public static BlockEntityType<FluidGaugeBlockEntity<ItemVariant>> ITEM_GAUGE;

    public static BlockEntityType<MetalBarrelBlockEntity> METAL_BARREL;

    public static BlockEntityType<DisplayPlatformBlockEntity> ITEM_BUFFER_BLOCK_ENTITY;
    public static BlockEntityType<InventoryDetectorBlockEntity> INVENTORY_DETECTOR;
    public static BlockEntityType<GlassTankBlockEntity> GLASS_TANK;
    public static BlockEntityType<MultiTankBlockEntity> MULTI_TANK;
    public static BlockEntityType<ItemDuctBlockEntity> ITEM_DUCT_BLOCK_ENTITY;
    public static BlockEntityType<TrommelBlockEntity> TROMMEL;
    public static BlockEntityType<MotorBlockEntity> MOTOR;
    public static BlockEntityType<AdvancedMotorBlockEntity> ADVANCED_MOTOR;
    public static BlockEntityType<LinearOscillatorBlockEntity> LINEAR_OSCILLATOR;
    public static BlockEntityType<DeployerBlockEntity> DEPLOYER;
    public static BlockEntityType<AgitatorBlockEntity> AGITATOR;
    public static BlockEntityType<ItemPortBlock.BlockEntity> VAT_ITEM_PORT;
    public static BlockEntityType<FluidPortBlock.BlockEntity> VAT_FLUID_PORT;
    public static BlockEntityType<MultiBlock.Entity> VAT_CASING;
    public static BlockEntityType<MultiBlock.Entity> VAT_WINDOW;
    public static BlockEntityType<VatControllerBlockEntity> VAT_CONTROLLER;

    public static BlockEntityType<ConverterBlockEntity> CONVERTER;
    public static BlockEntityType<ConverterBlockEntity> LARGE_CONVERTER;

    public static BlockEntityType<CharnelPumpBlockEntity> CHARNEL_PUMP;
    public static BlockEntityType<WellHeadBlockEntity> WELL_HEAD;
    public static BlockEntityType<WrithingEarthSpoutBlockEntity> WRITHING_EARTH_SPOUT;

    public static BlockEntityType<FluidDrainBlockEntity> FLUID_DRAIN;
    public static BlockEntityType<FluidInterfaceBlockEntity> FLUID_INTERFACE;
    public static BlockEntityType<IntegratorBlockEntity> INTEGRATOR;
    public static BlockEntityType<AdvancedIntegratorBlockEntity> ADVANCED_INTEGRATOR;
//    public static BlockEntityType<AdvancedIntegratorStructureBlockEntity> ADVANCED_INTEGRATOR_STRUCTURE;
    public static BlockEntityType<HeaterBlockEntity> HEATER;

    public static BlockEntityType<BigLeverBlockEntity> BIG_LEVER;

    public static BlockEntityType<ItemPipeBlockEntity> PNEUMATIC_PIPE;
    public static BlockEntityType<MergePipeBlockEntity> MERGE_ITEM_PIPE;
    public static BlockEntityType<BufferBlockEntity> BUFFER;
    public static BlockEntityType<ItemPumpBlockEntity> ITEM_PUMP;
    public static BlockEntityType<EjectorBlockEntity> EJECTOR;
    public static BlockEntityType<RouterBlockEntity> ROUTER;


    public static BlockEntityType<MixerBlockEntity> MIXER;
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
    public static BlockEntityType<BottlerBlockEntity> BOTTLER;
    public static BlockEntityType<TroughBlockEntity> FEEDING_TROUGH;
    public static BlockEntityType<PylonBlockEntity> PYLON;
    public static BlockEntityType<SynthesiserBlockEntity> SYNTHESISER;
    public static BlockEntityType<MincerBlockEnity> MINCER;
    public static BlockEntityType<HomogeniserBlockEntity> HOMOGENISER;
    public static BlockEntityType<FlameJetBlockEntity> FLAME_JET;
    public static BlockEntityType<ItemMincerBlockEntity> ITEM_MINCER;
    public static BlockEntityType<FluidRationerBlockEntity> FLUID_RATIONER;
    public static BlockEntityType<FluidExciterBlockEntity> FLUID_EXCITER;
    public static BlockEntityType<? extends SolidityDetectorBlockEntity> SOLIDITY_DETECTOR;
//    public static BlockEntityType<? extends MobPlatformBlockEntity> MOB_PLATFORM;
    public static BlockEntityType<? extends SurgeryPlatformBlockEntity> SURGERY_PLATFORM;
    public static BlockEntityType<UpgradeManagerBlockEntity> UPGRADE_MANAGER;

    public static BlockEntityType<? extends HoldingTrackBlock.HoldingTrackBlockEntity> HOLDING_TRACK;
    public static BlockEntityType<?> VASCULAR_CONDUIT;
    public static BlockEntityType<VSCBlockEntity> VSC;
    public static BlockEntityType<LargeMotorBlockEntity> LARGE_MOTOR;
    public static BlockEntityType<FlywheelBlockEntity> FLYWHEEL;

    public static <T extends net.minecraft.block.entity.BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block)
    {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, id),
                                 FabricBlockEntityTypeBuilder.create(factory, block).build());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void initialise()
    {

        // --- Fluid Transfer ---
        PUMP = register("pump_block_entity", PumpBlockEntity::new, FluidTransport.PUMP);
        FluidPump.SIDED.registerForBlockEntity(PumpBlockEntity::getPump, PUMP);

        TANK = register("tank_block_entity", (pos, state) -> new TankBlockEntity(TANK, pos, state, 8 * FluidConstants.BUCKET), FluidTransport.BASIC_TANK);
        FluidStorage.SIDED.registerForBlockEntity(TankBlockEntity::getStorage, TANK);
        TankBlockEntity.LOOKUP.registerForBlockEntity(TankBlockEntity::find, TANK);

        GLASS_TANK = register("glass_tank_block_entity", (pos, state) -> new GlassTankBlockEntity(pos, state, 8 * FluidConstants.BUCKET), FluidTransport.BASIC_GLASS_TANK);
        FluidStorage.SIDED.registerForBlockEntity(GlassTankBlockEntity::getStorage, GLASS_TANK);
        TankBlockEntity.LOOKUP.registerForBlockEntity(TankBlockEntity::find, GLASS_TANK);

        ADVANCED_TANK = register("advanced_tank", (pos, state) -> new TankBlockEntity(ADVANCED_TANK, pos, state, 16 * FluidConstants.BUCKET), FluidTransport.ADVANCED_TANK);
        FluidStorage.SIDED.registerForBlockEntity(TankBlockEntity::getStorage, ADVANCED_TANK);
        TankBlockEntity.LOOKUP.registerForBlockEntity(TankBlockEntity::find, ADVANCED_TANK);

        MULTI_TANK = register("multi_tank", MultiTankBlockEntity::new, FluidTransport.MULTI_TANK);
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage(), MULTI_TANK);
        Heatable.LOOKUP.registerSelf(MULTI_TANK);
        FLUID_BUFFER = register("fluid_buffer", FluidBufferBlockEntity::new, FluidTransport.FLUID_BUFFER);
        TableComponent.LOOKUP.registerForBlockEntity(FluidBufferBlockEntity::getTableComponent, FLUID_BUFFER);
        FLUID_PIPE = register("fluid_pipe", (pos, state) -> new FluidPipeBlockEntity<>(FLUID_PIPE, pos, state, BlockPipeVertex::new), FluidTransport.PIPE);
        STOP_VALVE = register("stop_valve", (pos, state) -> new FluidPipeBlockEntity<>(STOP_VALVE, pos, state, StopValveBlock.StopValvePipeVertex::new), FluidTransport.STOP_VALVE);
        CHECK_VALVE = register("check_valve", (pos, state) -> new FluidPipeBlockEntity<>(CHECK_VALVE, pos, state, CheckValveBlock.CheckValvePipeVertex::new), FluidTransport.CHECK_VALVE);
        FILTER_PIPE = register("filter_pipe", FilterPipeBlockEntity::new, FluidTransport.FILTER_PIPE);
        LIMITER_VALVE = register("limiter_valve", LimiterValveBlockEntity::new, FluidTransport.LIMITER_VALVE);
        WINDOW_PIPE = register("window_pipe", WindowPipeBlockEntity::new, FluidTransport.WINDOW_PIPE);

        FLUID_DRAIN = register("fluid_drain", FluidDrainBlockEntity::new, FluidTransport.FLUID_DRAIN);
        FLUID_INTERFACE = register("fluid_port", FluidInterfaceBlockEntity::new, FluidTransport.FLUID_INTERFACE);

        FLEX_TANK = register("flex_tank", (p, s) -> new FlexTankBlockEntity(FLEX_TANK, p, s, FluidTransport.FLEX_TANK.capacity), FluidTransport.FLEX_TANK);
        FluidStorage.SIDED.registerForBlockEntity(FlexTankBlockEntity::getStorage, FLEX_TANK);
        ADVANCED_FLEX_TANK = register("advanced_flex_tank", (p, s) -> new FlexTankBlockEntity(ADVANCED_FLEX_TANK, p, s, FluidTransport.ADVANCED_FLEX_TANK.capacity), FluidTransport.ADVANCED_FLEX_TANK);
        FluidStorage.SIDED.registerForBlockEntity(FlexTankBlockEntity::getStorage, ADVANCED_FLEX_TANK);

        FLUID_GAUGE = register("fluid_gauge", (p, s) -> new FluidGaugeBlockEntity<>(FLUID_GAUGE, p, s, FluidStorage.SIDED), FluidTransport.FLUID_GAUGE);
        ITEM_GAUGE = register("item_gauge", (p, s) -> new FluidGaugeBlockEntity<>(ITEM_GAUGE, p, s, ItemStorage.SIDED), FluidTransport.ITEM_GAUGE);

        HEATER = register("heater", HeaterBlockEntity::new, NMBlocks.HEATER);
        BloodAcceptor.SIDED.registerSelf(HEATER);

        VASCULAR_CONDUIT = register("vascular_conduit", (pos, state) -> new VascularConduitBlockEntity(VASCULAR_CONDUIT, pos, state), NMBlocks.VASCULAR_CONDUIT);
        VSC = register("vsc", (pos, state) -> new VSCBlockEntity(VSC, pos, state), NMBlocks.VSC);
        BloodAcceptor.SIDED.registerForBlockEntity(VSCBlockEntity::getBloodAcceptor, VSC);

        METAL_BARREL = register("metal_barrel", (pos, state) -> new MetalBarrelBlockEntity(METAL_BARREL, pos, state), NMBlocks.METAL_BARREL);

        // --- Surgery Machine ---
//        MOB_PLATFORM = registerBlockEntity("mob_platform", MobPlatformBlockEntity::new, NMBlocks.MOB_PLATFORM);
        ITEM_BUFFER_BLOCK_ENTITY = register("item_buffer", DisplayPlatformBlockEntity::new, NMBlocks.ITEM_BUFFER);
        TableComponent.LOOKUP.registerForBlockEntity(DisplayPlatformBlockEntity::getTableComponent, ITEM_BUFFER_BLOCK_ENTITY);
        MutateInPlace.ITEM.registerForBlockEntity(DisplayPlatformBlockEntity::getMip, ITEM_BUFFER_BLOCK_ENTITY);
        SURGERY_PLATFORM = register("surgery_platform", (p, s) -> new SurgeryPlatformBlockEntity(SURGERY_PLATFORM, p, s), NMBlocks.SURGERY_PLATFORM);
        TableComponent.LOOKUP.registerForBlockEntity(SurgeryPlatformBlockEntity::getTableComponent, SURGERY_PLATFORM);
        MutateInPlace.ENTITY.registerForBlockEntity(SurgeryPlatformBlockEntity::getMutate, SURGERY_PLATFORM);

        UPGRADE_MANAGER = register("upgrade_manager", (p, s) -> new UpgradeManagerBlockEntity(UPGRADE_MANAGER, p, s), NMBlocks.UPGRADE_MANAGER);

        // --- Item Transfer ---
        ITEM_DUCT_BLOCK_ENTITY = register("item_duct", ItemDuctBlockEntity::new, NMBlocks.ITEM_DUCT);
        PNEUMATIC_PIPE = register("pneumatic_pipe", ItemPipeBlockEntity::new, NMBlocks.PNEUMATIC_TUBE);
        MERGE_ITEM_PIPE = register("merge_item_pipe", MergePipeBlockEntity::new, NMBlocks.MERGE_ITEM_PIPE);
        BUFFER = register("buffer", BufferBlockEntity::new, NMBlocks.BUFFER);
        INVENTORY_DETECTOR = register("content_detector", InventoryDetectorBlockEntity::new, NMBlocks.CONTENT_DETECTOR);
        SOLIDITY_DETECTOR = register("solidity_detector", SolidityDetectorBlockEntity::new, NMBlocks.SOLIDITY_DETECTOR);
        ItemStorage.SIDED.registerForBlockEntity(InventoryDetectorBlockEntity::getStorage, INVENTORY_DETECTOR);
        EJECTOR = register("ejector", EjectorBlockEntity::new, NMBlocks.EJECTOR);
        ITEM_PUMP = register("item_pump", ItemPumpBlockEntity::new, NMBlocks.ITEM_PUMP);
        ROUTER = register("router", RouterBlockEntity::new, NMBlocks.ROUTER);
        DUMPER = register("dumper", DumperBlockEntity::new, NMBlocks.DUMPER);
        ItemStorage.SIDED.registerForBlockEntity(DumperBlockEntity::getStorage, DUMPER);

        // --- Machines ---
        INTEGRATOR = register("integrator_egg", IntegratorBlockEntity::new, NMBlocks.INTEGRATOR_EGG);
        ItemStorage.SIDED.registerForBlockEntity(IntegratorBlockEntity::getItemStorage, INTEGRATOR);

        ADVANCED_INTEGRATOR = register("advanced_integrator", (pos, state) -> new AdvancedIntegratorBlockEntity(ADVANCED_INTEGRATOR, pos, state), NMBlocks.ADVANCED_INTEGRATOR);
//        ADVANCED_INTEGRATOR_STRUCTURE = register("advanced_integrator_structure",
//                (pos, state) -> new AdvancedIntegratorStructureBlockEntity(ADVANCED_INTEGRATOR_STRUCTURE, pos, state), NMBlocks.ADVANCED_INTEGRATOR_STRUCTURE);
        DataPort.DATA_PORT.registerForBlockEntity(AdvancedIntegratorStructureBlockEntity::getPort, NMBlocks.ADVANCED_INTEGRATOR.getStructure().getBlockEntityType());

//        TROMMEL = register("trommel", TrommelBlockEntity::new, NMBlocks.TROMMEL);
//        TROMMEL_STRUCTURE = register("trommel_structure", TrommelStructureBlockEntity::new, NMBlocks.TROMMEL_STRUCTURE);
        SMALL_TROMMEL = register("small_trommel", SmallTrommelBlockEntity::new, NMBlocks.SMALL_TROMMEL);
        SMALL_TROMMEL_STRUCTURE = register("small_trommel_structure", SmallTrommelBlockEntity.Structure::new, NMBlocks.SMALL_TROMMEL_STRUCTURE);
        FluidStorage.SIDED.registerForBlockEntity(SmallTrommelBlockEntity::getInputStorage, SMALL_TROMMEL);
        FluidStorage.SIDED.registerForBlocks(SmallTrommelBlock.Structure::getFluidStorage, NMBlocks.SMALL_TROMMEL_STRUCTURE);
        ItemStorage.SIDED.registerForBlocks(SmallTrommelBlock.Structure::getItemStorage, NMBlocks.SMALL_TROMMEL_STRUCTURE);

        BIG_LEVER = register("big_lever", BigLeverBlockEntity::new, NMBlocks.BIG_LEVER);

        MOTOR = register("motor_unit", (pos, state) -> new MotorBlockEntity(MOTOR, pos, state), NMBlocks.MOTOR);
        FluidStorage.SIDED.registerForBlockEntity(LiquidFuelMachine::getTank, MOTOR);

        ADVANCED_MOTOR = register("advanced_motor", (pos, state) -> new AdvancedMotorBlockEntity(ADVANCED_MOTOR, pos, state), NMBlocks.ADVANCED_MOTOR);
        BloodAcceptor.SIDED.registerForBlockEntity(AdvancedMotorBlockEntity::getBloodAcceptor, ADVANCED_MOTOR);

        LARGE_MOTOR = register("large_motor", (p, s) -> new LargeMotorBlockEntity(LARGE_MOTOR, p, s), NMBlocks.LARGE_MOTOR);
        BloodAcceptor.SIDED.registerForBlockEntity(LargeMotorBlockEntity::getAcceptor, LARGE_MOTOR);
        BloodAcceptor.SIDED.registerForBlockEntity(LargeMotorStructureEntity::getBloodAcceptor, NMBlocks.LARGE_MOTOR.getStructure().getBlockEntityType());

        FLYWHEEL = register("large_flywheel", (p, s) -> new FlywheelBlockEntity(FLYWHEEL, p, s), NMBlocks.FLYWHEEL);

        STIRLING_ENGINE = register("stirling_engine", StirlingEngineBlockEntity::new, NMBlocks.STIRLING_ENGINE);
        LINEAR_OSCILLATOR = register("linear_oscillator", LinearOscillatorBlockEntity::new, NMBlocks.LINEAR_OSCILLATOR);
        DEPLOYER = register("deployer", (p, s) -> new DeployerBlockEntity(DEPLOYER, p, s), NMBlocks.DEPLOYER);
        ItemStorage.SIDED.registerSelf(DEPLOYER);
        AGITATOR = register("agitator", AgitatorBlockEntity::new, NMBlocks.AGITATOR);

        GRINDER = register("grinder", GrinderBlockEntity::new, NMBlocks.GRINDER);
        ALLOY_KILN = register("alloy_kiln", AlloyKilnBlockEntity::new, NMBlocks.ALLOY_KILN);
        Heatable.LOOKUP.registerSelf(ALLOY_KILN);
        CRUCIBLE = register("crucible", CrucibleBlockEntity::new, NMBlocks.CRUCIBLE);
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getStorage(direction), CRUCIBLE);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getItemStorage(direction), CRUCIBLE);
        ALEMBIC = register("collector", AlembicBlockEntity::new, NMBlocks.COLLECTOR);
        FluidStorage.SIDED.registerForBlockEntity(AlembicBlockEntity::getStorage, ALEMBIC);

        CASTING_BASIN = register("casting_basin", CastingBasinBlockEntity::new, NMBlocks.CASTING_BASIN);
        FluidStorage.SIDED.registerForBlockEntity((be, dir) -> be.getStorage().fluid(dir), CASTING_BASIN);
        ItemStorage.SIDED.registerForBlockEntity((be, dir) -> be.getStorage().item(dir), CASTING_BASIN);

        HYDRAULIC_PRESS = register("hydraulic_press", HydraulicPressBlockEntity::new, NMBlocks.HYDRAULIC_PRESS);
//        FluidStorage.SIDED.registerForBlockEntity(HydraulicPressBlockEntity::getFluidStorageFromTop, HYDRAULIC_PRESS.);
        FluidStorage.SIDED.registerForBlocks(HydraulicPressBlockEntity::getFluidStorageFromTop, NMBlocks.HYDRAULIC_PRESS.getStructureBlock());

        PYLON = register("pylon", PylonBlockEntity::new, NMBlocks.PYLON);
        DataPort.DATA_PORT.registerForBlockEntity(PylonBlockEntity::getPort, PYLON);

        SYNTHESISER = register("synthesiser", SynthesiserBlockEntity::new , NMBlocks.SYNTHESISER);
        FluidStorage.SIDED.registerForBlockEntity(SynthesiserStorage::getFluidStorage, SYNTHESISER);

//        ItemStorage.SIDED.registerForBlockEntity(TableControllerBlockEntity::getStorage, PLC);
//        FluidStorage.SIDED.registerForBlockEntity(BloodMachineBlockEntity::getBuffer, PLC);

        CHARNEL_PUMP = register("charnel_pump", (p, s) -> new CharnelPumpBlockEntity(CHARNEL_PUMP, p, s), NMBlocks.CHARNEL_PUMP);
        BloodAcceptor.SIDED.registerForBlockEntity(CharnelPumpStructure.CPSBlockEntity::getAcceptor, NMBlocks.CHARNEL_PUMP.getStructure().getBlockEntityType());
        FluidStorage.SIDED.registerForBlockEntity(CharnelPumpStructure.CPSBlockEntity::getFluidStorage, NMBlocks.CHARNEL_PUMP.getStructure().getBlockEntityType());
        WELL_HEAD = register("well_head", (p, s) -> new WellHeadBlockEntity(WELL_HEAD, p, s), NMBlocks.WELL_HEAD);
        FluidStorage.SIDED.registerForBlockEntity(WellHeadBlockEntity::getFluidStorage, WELL_HEAD);
        FluidPump.SIDED.registerForBlockEntity(WellHeadBlockEntity::getFluidPump, WELL_HEAD);
        WRITHING_EARTH_SPOUT = register("writhing_earth_spout", (p, s) -> new WrithingEarthSpoutBlockEntity(WRITHING_EARTH_SPOUT, p, s), NMBlocks.WRITHING_EARTH_SPOUT);

        VAT_WINDOW = register("vat_window", (pos, state) -> new MultiBlock.Entity(VAT_WINDOW, pos, state), NMBlocks.VAT_WINDOW);
        VAT_CASING = register("vat_casing", (pos, state) -> new MultiBlock.Entity(VAT_CASING, pos, state), NMBlocks.VAT_CASING);
        VAT_ITEM_PORT = register("vat_item_port", ItemPortBlock.BlockEntity::new, NMBlocks.VAT_ITEM_PORT);
        VAT_FLUID_PORT = register("vat_fluid_port", FluidPortBlock.BlockEntity::new, NMBlocks.VAT_FLUID_PORT);
        VAT_CONTROLLER = register("vat_controller", VatControllerBlockEntity::new, NMBlocks.VAT_CONTROLLER);

        FLUID_EXCITER = register("fluid_exciter", (pos, state) -> new FluidExciterBlockEntity(FLUID_EXCITER, pos, state), NMBlocks.FLUID_EXCITER);
        FluidStorage.SIDED.registerForBlockEntity(FluidExciterBlockEntity::getInputStorage, FLUID_EXCITER);
//        FluidStorage.SIDED.registerForBlocks((world, pos, state, be, context) -> world.getBlockEntity(pos.down()) instanceof FluidExciterBlockEntity fbe ? fbe.getOutputStorage(context) : null, NMBlocks.FLUID_EXCITER.getStructureBlock());
//        FluidPump.SIDED.registerForBlocks(FluidExciterBlockEntity::getPump, NMBlocks.FLUID_EXCITER.getStructureBlock());
        BloodAcceptor.SIDED.registerForBlocks(FluidExciterBlockEntity::getBloodAcceptorFromTop, NMBlocks.FLUID_EXCITER.getStructureBlock());

        MIXER = register("mixer", MixerBlockEntity::new, NMBlocks.MIXER);
//        MIXER_TOP = registerBlockEntity("mixer_top", MixerTopBlockEntity::new, NMBlocks.MIXER_TOP);
        FluidStorage.SIDED.registerForBlockEntity(MixerBlockEntity::getFluidStorage, MIXER);
        FluidStorage.SIDED.registerForBlocks(MixerBlockEntity::getFluidStorageFromTop, NMBlocks.MIXER.getStructureBlock());
        ItemStorage.SIDED.registerForBlockEntity(MixerBlockEntity::getItemStorage, MIXER);
        ItemStorage.SIDED.registerForBlocks(MixerBlockEntity::getItemStorageFromTop, NMBlocks.MIXER.getStructureBlock());

        TRANSDUCER = register("transducer", TransducerBlockEntity::new, NMBlocks.TRANSDUCER);
        FluidStorage.SIDED.registerForBlockEntity(TransducerBlockEntity::getStorage, TRANSDUCER);
        FluidPump.SIDED.registerForBlockEntity(TransducerBlockEntity::getPump, TRANSDUCER);
//        BloodAcceptor.SIDED.registerForBlockEntity(TransducerBlockEntity::getBloodAcceptor, TRANSDUCER);

        PEDESTAL = register("pedestal", PedestalBlockEntity::new, NMBlocks.PEDESTAL);
        ItemStorage.SIDED.registerForBlockEntity(PedestalBlockEntity::getStorage, PEDESTAL);
        MutateInPlace.ITEM.registerForBlockEntity(PedestalBlockEntity::getMutateInPlace, PEDESTAL);

        ASSEMBLER = register("assembler", AssemblerBlockEntity::new, NMBlocks.ASSEMBLER);
        ItemStorage.SIDED.registerForBlockEntity((be, dir) -> be.getStorage().getStorage(dir, false), ASSEMBLER);
        BloodAcceptor.SIDED.registerForBlocks(AssemblerBlockEntity::getBloodAcceptorFromTop, NMBlocks.ASSEMBLER.getStructureBlock());

        WORKSTATION = register("workstation", WorkstationBlockEntity::new, NMBlocks.WORKSTATION);

        DEATH_BLADES = register("death_blades", DeathBladesBlockEntity::new, NMBlocks.DEATH_BLADES);

        BOTTLER = register("bottler", BottlerBlockEntity::new, NMBlocks.BOTTLER);
        ItemStorage.SIDED.registerForBlockEntity(BottlerBlockEntity::getItemStorage, BOTTLER);
//        FluidStorage.SIDED.registerForBlockEntity(BottlerBlockEntity::getStorage, BOTTLER);

        FEEDING_TROUGH = register("feeding_trough", TroughBlockEntity::new, NMBlocks.FEEDING_TROUGH);
        FluidStorage.SIDED.registerForBlockEntity(TroughBlockEntity::getStorage, FEEDING_TROUGH);

        MINCER = register("mincer", MincerBlockEnity::new, NMBlocks.MINCER);
        FluidStorage.SIDED.registerForBlockEntity(MincerBlockEnity::getFluidStorage, MINCER);

        HOMOGENISER = register("homogeniser", (pos, state) -> new HomogeniserBlockEntity(HOMOGENISER, pos, state), NMBlocks.HOMOGENISER);
        ItemStorage.SIDED.registerForBlockEntity(HomogeniserBlockEntity::getItemStorage, HOMOGENISER);

        FLAME_JET = register("flame_jet", FlameJetBlockEntity::new, NMBlocks.FLAME_JET);
        FluidStorage.SIDED.registerForBlockEntity(FlameJetBlockEntity::getFluidStorage, FLAME_JET);

        ITEM_MINCER = register("item_mincer", ItemMincerBlockEntity::new, NMBlocks.ITEM_MINCER);
        ItemStorage.SIDED.registerForBlockEntity(ItemMincerBlockEntity::getInputStorage, ITEM_MINCER);
        FluidStorage.SIDED.registerForBlockEntity(ItemMincerBlockEntity::getOutputStorage, ITEM_MINCER);
        FluidPump.SIDED.registerForBlockEntity(ItemMincerBlockEntity::getPump, ITEM_MINCER);

        FLUID_RATIONER = register("fluid_rationer", FluidRationerBlockEntity::new, NMBlocks.FLUID_RATIONER);
        FluidStorage.SIDED.registerForBlockEntity(FluidRationerBlockEntity::getStorage, FLUID_RATIONER);
        FluidPump.SIDED.registerForBlockEntity(FluidRationerBlockEntity::getPump, FLUID_RATIONER);


        HOLDING_TRACK = register("holding_track", (p, s) -> new HoldingTrackBlock.HoldingTrackBlockEntity(HOLDING_TRACK, p, s), NMBlocks.HOLDING_TRACK);

        ItemStorage.SIDED.registerSelf(BUFFER);
        FluidStorage.SIDED.registerSelf(FLUID_INTERFACE);
        ItemStorage.SIDED.registerSelf(ITEM_DUCT_BLOCK_ENTITY);

        ItemStorage.SIDED.registerSelf(VAT_ITEM_PORT);
        FluidStorage.SIDED.registerSelf(VAT_FLUID_PORT);


        FluidStorage.SIDED.registerForBlockEntity(PumpBlockEntity::getBuffer, PUMP);


        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getItemStorage(direction), GRINDER);

        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getStorage(direction), ALLOY_KILN);
        ItemStorage.SIDED.registerForBlockEntity(DisplayPlatformBlockEntity::getStorage, ITEM_BUFFER_BLOCK_ENTITY);

        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getFuelStorage(direction), STIRLING_ENGINE);

        // --- Furnaces ---

        Heatable.LOOKUP.registerFallback((world, pos, state, blockEntity, context) ->
        {
            if (blockEntity instanceof AbstractFurnaceBlockEntity furnace)
            {
                return HeatableFurnace.of(furnace);
            }
            return null;
        });

        MotorisedBlock.LOOKUP.registerFallback((world, pos, state, blockEntity, context) ->
        {
            if (blockEntity instanceof MotorisedBlock motorisedBlock)
            {
                return motorisedBlock;
            }
            return null;
        });


        // TODO: Remove
        FluidStorage.SIDED.registerFallback((world, pos, state, be, direction) ->
        {
            if (be instanceof FluidBuffer.FluidBufferProvider provider)
            {
                return provider.getBuffer(direction);
            }
            return null;
        });

        BloodAcceptor.SIDED.registerForBlocks(((world, pos, state, blockEntity, context) -> new BloodAcceptor()
        {
            @Override
            public Mode getMode()
            {
                return Mode.SOURCE;
            }

            @Override
            public long getOutput()
            {
                return 100;
            }
        }), NMBlocks.POWER_EMITTER);
    }
}
