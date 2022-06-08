package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.blockentity.*;
import com.neep.neepmeat.blockentity.fluid.*;
import com.neep.neepmeat.blockentity.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.blockentity.machine.*;
import com.neep.neepmeat.blockentity.pipe.PneumaticPipeBlockEntity;
import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMBlockEntities
{
    public static BlockEntityType<?> NODE_BLOCK_ENTITY;
    public static BlockEntityType<PumpBlockEntity> PUMP_BLOCK_ENTITY;
    public static BlockEntityType<TankBlockEntity> TANK_BLOCK_ENTITY;
    public static BlockEntityType<FluidBufferBlockEntity> FLUID_BUFFER;
    public static BlockEntityType<CheckValveBlockEntity> CHECK_VALVE;
    public static BlockEntityType<StopValveBlockEntity> STOP_VALVE;

    public static BlockEntityType<ItemBufferBlockEntity> ITEM_BUFFER_BLOCK_ENTITY;
    public static BlockEntityType<ContentDetectorBlockEntity> CONTENT_DETECTOR;
    public static BlockEntityType<GlassTankBlockEntity> GLASS_TANK_BLOCK_ENTITY;
    public static BlockEntityType<ItemDuctBlockEntity> ITEM_DUCT_BLOCK_ENTITY;
    public static BlockEntityType<TrommelBlockEntity> TROMMEL_BLOCK_ENTITY;
    public static BlockEntityType<MotorBlockEntity> MOTOR;
    public static BlockEntityType<LinearOscillatorBlockEntity> LINEAR_OSCILLATOR;
    public static BlockEntityType<DeployerBlockEntity> DEPLOYER;
    public static BlockEntityType<AgitatorBlockEntity> AGITATOR;

    public static BlockEntityType<ConverterBlockEntity> CONVERTER;
    public static BlockEntityType<ConverterBaseBlockEntity> CONVERTER_BASE;
    public static BlockEntityType<ConverterBlockEntity> LARGE_CONVERTER;

    public static BlockEntityType<FluidDrainBlockEntity> FLUID_DRAIN;
    public static BlockEntityType<FluidPortBlockEntity> FLUID_PORT;
    public static BlockEntityType<IntegratorBlockEntity> INTEGRATOR;
    public static BlockEntityType<HeaterBlockEntity> HEATER;
    public static BlockEntityType<SpigotBlockEntity> SPIGOT;

    public static BlockEntityType<BigLeverBlockEntity> BIG_LEVER;

    public static BlockEntityType<PneumaticPipeBlockEntity> PNEUMATIC_PIPE;
    public static BlockEntityType<BufferBlockEntity> BUFFER;
    public static BlockEntityType<ItemPumpBlockEntity> ITEM_PUMP;
    public static BlockEntityType<EjectorBlockEntity> EJECTOR;

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block)
    {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, id),
                FabricBlockEntityTypeBuilder.create(factory, block).build());
    }

    public static void initialise()
    {
        // --- Fluid Transfer ---
        PUMP_BLOCK_ENTITY = registerBlockEntity("pump_block_entity", PumpBlockEntity::new, NMBlocks.PUMP);
        TANK_BLOCK_ENTITY = registerBlockEntity("tank_block_entity", TankBlockEntity::new, NMBlocks.TANK);
        FLUID_BUFFER = registerBlockEntity("fluid_buffer", FluidBufferBlockEntity::new, NMBlocks.FLUID_BUFFER);
        GLASS_TANK_BLOCK_ENTITY = registerBlockEntity("glass_tank_block_entity", GlassTankBlockEntity::new, NMBlocks.GLASS_TANK);
        NODE_BLOCK_ENTITY = registerBlockEntity("node_storage", NodeContainerBlockEntity::new, NMBlocks.PIPE);
        STOP_VALVE = registerBlockEntity("stop_valve", StopValveBlockEntity::new, NMBlocks.STOP_VALVE);

        FLUID_DRAIN = registerBlockEntity("fluid_drain", FluidDrainBlockEntity::new, NMBlocks.FLUID_DRAIN);
        FLUID_PORT = registerBlockEntity("fluid_port", FluidPortBlockEntity::new, NMBlocks.FLUID_PORT);
        HEATER = registerBlockEntity("heater", HeaterBlockEntity::new, NMBlocks.HEATER);
        SPIGOT = registerBlockEntity("spigot", SpigotBlockEntity::new, NMBlocks.SPIGOT);

        // --- Item Transfer ---
        ITEM_DUCT_BLOCK_ENTITY = registerBlockEntity("item_duct", ItemDuctBlockEntity::new, NMBlocks.ITEM_DUCT);
        ITEM_BUFFER_BLOCK_ENTITY = registerBlockEntity("item_buffer", ItemBufferBlockEntity::new, NMBlocks.ITEM_BUFFER);
        PNEUMATIC_PIPE = registerBlockEntity("pneumatic_pipe", PneumaticPipeBlockEntity::new, NMBlocks.PNEUMATIC_TUBE);
        BUFFER = registerBlockEntity("buffer", BufferBlockEntity::new, NMBlocks.BUFFER);
        CONTENT_DETECTOR = registerBlockEntity("content_detector", ContentDetectorBlockEntity::new, NMBlocks.CONTENT_DETECTOR);
        EJECTOR = registerBlockEntity("ejector", EjectorBlockEntity::new, NMBlocks.EJECTOR);
        ITEM_PUMP = registerBlockEntity("item_pump", ItemPumpBlockEntity::new, NMBlocks.ITEM_PUMP);

        // --- Machines ---
        INTEGRATOR = registerBlockEntity("integrator_egg", IntegratorBlockEntity::new, NMBlocks.INTEGRATOR_EGG);
        TROMMEL_BLOCK_ENTITY = registerBlockEntity("trommel", TrommelBlockEntity::new, NMBlocks.TROMMEL);
        BIG_LEVER = registerBlockEntity("big_lever", BigLeverBlockEntity::new, NMBlocks.BIG_LEVER);
        MOTOR = registerBlockEntity("motor_unit", MotorBlockEntity::new, NMBlocks.MOTOR);
        LINEAR_OSCILLATOR = registerBlockEntity("linear_oscillator", LinearOscillatorBlockEntity::new, NMBlocks.LINEAR_OSCILLATOR);
        DEPLOYER = registerBlockEntity("deployer", DeployerBlockEntity::new, NMBlocks.DEPLOYER);
        AGITATOR = registerBlockEntity("agitator", AgitatorBlockEntity::new, NMBlocks.AGITATOR);

        CONVERTER = registerBlockEntity("converter", ConverterBlockEntity::new, NMBlocks.CONVERTER);
        CONVERTER_BASE = registerBlockEntity("converter_base", ConverterBaseBlockEntity::new, NMBlocks.CONVERTER_BASE);
//        LARGE_CONVERTER = registerBlockEntity("large_converter", LargeConverterBlockEntity::new, NMBlocks.LARGE_CONVERTER);

        ItemStorage.SIDED.registerSelf(ITEM_BUFFER_BLOCK_ENTITY);
        ItemStorage.SIDED.registerSelf(TROMMEL_BLOCK_ENTITY);
        ItemStorage.SIDED.registerSelf(BUFFER);
        FluidStorage.SIDED.registerSelf(FLUID_PORT);

        ItemStorage.SIDED.registerSelf(CONVERTER_BASE);
        ItemStorage.SIDED.registerSelf(DEPLOYER);
        ItemStorage.SIDED.registerSelf(ITEM_DUCT_BLOCK_ENTITY);

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
