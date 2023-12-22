package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.multiblock.IMultiBlock;
import com.neep.neepmeat.block.vat.FluidPortBlock;
import com.neep.neepmeat.block.vat.ItemPortBlock;
import com.neep.neepmeat.blockentity.*;
import com.neep.neepmeat.blockentity.fluid.*;
import com.neep.neepmeat.blockentity.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.blockentity.machine.*;
import com.neep.neepmeat.machine.grinder.GrinderBlockEntity;
import com.neep.neepmeat.machine.mixer.MixerBlockEntity;
import com.neep.neepmeat.blockentity.pipe.MergePipeBlockEntity;
import com.neep.neepmeat.blockentity.pipe.PneumaticPipeBlockEntity;
import com.neep.neepmeat.blockentity.pipe.RouterBlockEntity;
import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.machine.mixer.MixerTopBlockEntity;
import com.neep.neepmeat.machine.motor.MotorBlockEntity;
import com.neep.neepmeat.machine.multitank.MultiTankBlockEntity;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineBlockEntity;
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

    public static BlockEntityType<ItemBufferBlockEntity> ITEM_BUFFER_BLOCK_ENTITY;
    public static BlockEntityType<ContentDetectorBlockEntity> CONTENT_DETECTOR;
    public static BlockEntityType<GlassTankBlockEntity> GLASS_TANK_BLOCK_ENTITY;
    public static BlockEntityType<MultiTankBlockEntity> MULTI_TANK;
    public static BlockEntityType<ItemDuctBlockEntity> ITEM_DUCT_BLOCK_ENTITY;
    public static BlockEntityType<TrommelBlockEntity> TROMMEL_BLOCK_ENTITY;
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
    public static BlockEntityType<ConverterBaseBlockEntity> CONVERTER_BASE;
    public static BlockEntityType<ConverterBlockEntity> LARGE_CONVERTER;

    public static BlockEntityType<FluidDrainBlockEntity> FLUID_DRAIN;
    public static BlockEntityType<FluidInterfaceBlockEntity> FLUID_INTERFACE;
    public static BlockEntityType<IntegratorBlockEntity> INTEGRATOR;
    public static BlockEntityType<HeaterBlockEntity> HEATER;
    public static BlockEntityType<SpigotBlockEntity> SPIGOT;

    public static BlockEntityType<BigLeverBlockEntity> BIG_LEVER;

    public static BlockEntityType<PneumaticPipeBlockEntity> PNEUMATIC_PIPE;
    public static BlockEntityType<MergePipeBlockEntity> MERGE_ITEM_PIPE;
    public static BlockEntityType<BufferBlockEntity> BUFFER;
    public static BlockEntityType<ItemPumpBlockEntity> ITEM_PUMP;
    public static BlockEntityType<EjectorBlockEntity> EJECTOR;
    public static BlockEntityType<RouterBlockEntity> ROUTER;

    public static BlockEntityType<MixerBlockEntity> MIXER;
    public static BlockEntityType<MixerTopBlockEntity> MIXER_TOP;
    public static BlockEntityType<GrinderBlockEntity> GRINDER;
    public static BlockEntityType<StirlingEngineBlockEntity> STIRLING_ENGINE;

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
        TANK_BLOCK_ENTITY = registerBlockEntity("tank_block_entity", TankBlockEntity::new, NMBlocks.TANK);
        MULTI_TANK = registerBlockEntity("multi_tank", MultiTankBlockEntity::new, NMBlocks.MULTI_TANK);
        FLUID_BUFFER = registerBlockEntity("fluid_buffer", FluidBufferBlockEntity::new, NMBlocks.FLUID_BUFFER);
        GLASS_TANK_BLOCK_ENTITY = registerBlockEntity("glass_tank_block_entity", GlassTankBlockEntity::new, NMBlocks.GLASS_TANK);
        NODE_BLOCK_ENTITY = registerBlockEntity("node_storage", NodeContainerBlockEntity::new, NMBlocks.PIPE);
        STOP_VALVE = registerBlockEntity("stop_valve", StopValveBlockEntity::new, NMBlocks.STOP_VALVE);

        FLUID_DRAIN = registerBlockEntity("fluid_drain", FluidDrainBlockEntity::new, NMBlocks.FLUID_DRAIN);
        FLUID_INTERFACE = registerBlockEntity("fluid_port", FluidInterfaceBlockEntity::new, NMBlocks.FLUID_INTERFACE);
        HEATER = registerBlockEntity("heater", HeaterBlockEntity::new, NMBlocks.HEATER);
        SPIGOT = registerBlockEntity("spigot", SpigotBlockEntity::new, NMBlocks.SPIGOT);

        // --- Item Transfer ---
        ITEM_DUCT_BLOCK_ENTITY = registerBlockEntity("item_duct", ItemDuctBlockEntity::new, NMBlocks.ITEM_DUCT);
        ITEM_BUFFER_BLOCK_ENTITY = registerBlockEntity("item_buffer", ItemBufferBlockEntity::new, NMBlocks.ITEM_BUFFER);
        PNEUMATIC_PIPE = registerBlockEntity("pneumatic_pipe", PneumaticPipeBlockEntity::new, NMBlocks.PNEUMATIC_TUBE);
        MERGE_ITEM_PIPE = registerBlockEntity("merge_item_pipe", MergePipeBlockEntity::new, NMBlocks.MERGE_ITEM_PIPE);
        BUFFER = registerBlockEntity("buffer", BufferBlockEntity::new, NMBlocks.BUFFER);
        CONTENT_DETECTOR = registerBlockEntity("content_detector", ContentDetectorBlockEntity::new, NMBlocks.CONTENT_DETECTOR);
        EJECTOR = registerBlockEntity("ejector", EjectorBlockEntity::new, NMBlocks.EJECTOR);
        ITEM_PUMP = registerBlockEntity("item_pump", ItemPumpBlockEntity::new, NMBlocks.ITEM_PUMP);
        ROUTER = registerBlockEntity("router", RouterBlockEntity::new, NMBlocks.ROUTER);

        // --- Machines ---
        INTEGRATOR = registerBlockEntity("integrator_egg", IntegratorBlockEntity::new, NMBlocks.INTEGRATOR_EGG);
        TROMMEL_BLOCK_ENTITY = registerBlockEntity("trommel", TrommelBlockEntity::new, NMBlocks.TROMMEL);
        BIG_LEVER = registerBlockEntity("big_lever", BigLeverBlockEntity::new, NMBlocks.BIG_LEVER);
        MOTOR = registerBlockEntity("motor_unit", MotorBlockEntity::new, NMBlocks.MOTOR);
        STIRLING_ENGINE = registerBlockEntity("stirling_engine", StirlingEngineBlockEntity::new, NMBlocks.STIRLING_ENGINE);
        LINEAR_OSCILLATOR = registerBlockEntity("linear_oscillator", LinearOscillatorBlockEntity::new, NMBlocks.LINEAR_OSCILLATOR);
        DEPLOYER = registerBlockEntity("deployer", DeployerBlockEntity::new, NMBlocks.DEPLOYER);
        AGITATOR = registerBlockEntity("agitator", AgitatorBlockEntity::new, NMBlocks.AGITATOR);

        GRINDER = registerBlockEntity("grinder", GrinderBlockEntity::new, NMBlocks.GRINDER);

        VAT_WINDOW = registerBlockEntity("vat_window", (pos, state) -> new IMultiBlock.Entity(VAT_WINDOW, pos, state), NMBlocks.VAT_WINDOW);
        VAT_CASING = registerBlockEntity("vat_casing", (pos, state) -> new IMultiBlock.Entity(VAT_CASING, pos, state), NMBlocks.VAT_CASING);
//        VAT_CASING = registerBlockEntity("vat_casing", IMultiBlock.Entity.createFactory(VAT_CASING), NMBlocks.VAT_CASING);
        VAT_ITEM_PORT = registerBlockEntity("vat_item_port", ItemPortBlock.BlockEntity::new, NMBlocks.VAT_ITEM_PORT);
        VAT_FLUID_PORT = registerBlockEntity("vat_fluid_port", FluidPortBlock.BlockEntity::new, NMBlocks.VAT_FLUID_PORT);
        VAT_CONTROLLER = registerBlockEntity("vat_controller", VatControllerBlockEntity::new, NMBlocks.VAT_CONTROLLER);

        CONVERTER = registerBlockEntity("converter", ConverterBlockEntity::new, NMBlocks.CONVERTER);
        CONVERTER_BASE = registerBlockEntity("converter_base", ConverterBaseBlockEntity::new, NMBlocks.CONVERTER_BASE);
//        LARGE_CONVERTER = registerBlockEntity("large_converter", LargeConverterBlockEntity::new, NMBlocks.LARGE_CONVERTER);

        MIXER = registerBlockEntity("mixer", MixerBlockEntity::new, NMBlocks.MIXER);
        MIXER_TOP = registerBlockEntity("mixer_top", MixerTopBlockEntity::new, NMBlocks.MIXER_TOP);

        ItemStorage.SIDED.registerSelf(ITEM_BUFFER_BLOCK_ENTITY);
        ItemStorage.SIDED.registerSelf(TROMMEL_BLOCK_ENTITY);
        ItemStorage.SIDED.registerSelf(BUFFER);
        FluidStorage.SIDED.registerSelf(FLUID_INTERFACE);
        ItemStorage.SIDED.registerSelf(CONVERTER_BASE);
        ItemStorage.SIDED.registerSelf(DEPLOYER);
        ItemStorage.SIDED.registerSelf(ITEM_DUCT_BLOCK_ENTITY);

        ItemStorage.SIDED.registerSelf(VAT_ITEM_PORT);
        FluidStorage.SIDED.registerSelf(VAT_FLUID_PORT);


        FluidStorage.SIDED.registerForBlockEntity(PumpBlockEntity::getBuffer, PUMP);

        FluidStorage.SIDED.registerForBlockEntity(MixerBlockEntity::getFluidStorage, MIXER);
        ItemStorage.SIDED.registerForBlockEntity(MixerBlockEntity::getItemStorage, MIXER);
        FluidStorage.SIDED.registerForBlocks(MixerTopBlockEntity::getBottomStorage, NMBlocks.MIXER_TOP);

        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getStorage().getItemStorage(direction), GRINDER);

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
