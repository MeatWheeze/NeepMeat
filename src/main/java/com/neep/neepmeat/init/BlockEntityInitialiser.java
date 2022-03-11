package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.blockentity.*;
import com.neep.neepmeat.blockentity.fluid.*;
import com.neep.neepmeat.blockentity.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.blockentity.machine.HeaterBlockEntity;
import com.neep.neepmeat.fluid_util.FluidBuffer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEntityInitialiser
{
    public static BlockEntityType<?> NODE_BLOCK_ENTITY;
    public static BlockEntityType<PumpBlockEntity> PUMP_BLOCK_ENTITY;
    public static BlockEntityType<TankBlockEntity> TANK_BLOCK_ENTITY;
    public static BlockEntityType<ItemBufferBlockEntity> ITEM_BUFFER_BLOCK_ENTITY;
    public static BlockEntityType<GlassTankBlockEntity> GLASS_TANK_BLOCK_ENTITY;
    public static BlockEntityType<ItemDuctBlockEntity> ITEM_DUCT_BLOCK_ENTITY;
    public static BlockEntityType<TrommelBlockEntity> TROMMEL_BLOCK_ENTITY;
    public static BlockEntityType<FluidDrainBlockEntity> FLUID_DRAIN;
    public static BlockEntityType<FluidPortBlockEntity> FLUID_PORT;
    public static BlockEntityType<IntegratorBlockEntity> INTEGRATOR;
    public static BlockEntityType<HeaterBlockEntity> HEATER;
    public static BlockEntityType<SpigotBlockEntity> SPIGOT;

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block)
    {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, id),
                FabricBlockEntityTypeBuilder.create(factory, block).build());
    }

    public static void initialiseBlockEntities()
    {
        PUMP_BLOCK_ENTITY = registerBlockEntity("pump_block_entity", PumpBlockEntity::new, BlockInitialiser.PUMP);
        TANK_BLOCK_ENTITY = registerBlockEntity("tank_block_entity", TankBlockEntity::new, BlockInitialiser.TANK);
        GLASS_TANK_BLOCK_ENTITY = registerBlockEntity("glass_tank_block_entity", GlassTankBlockEntity::new, BlockInitialiser.GLASS_TANK);
        NODE_BLOCK_ENTITY = registerBlockEntity("node_storage", NodeContainerBlockEntity::new, BlockInitialiser.PIPE);
        ITEM_DUCT_BLOCK_ENTITY = registerBlockEntity("item_duct", ItemDuctBlockEntity::new, BlockInitialiser.ITEM_DUCT);
        ITEM_BUFFER_BLOCK_ENTITY = registerBlockEntity("item_buffer", ItemBufferBlockEntity::new, BlockInitialiser.ITEM_BUFFER);
        TROMMEL_BLOCK_ENTITY = registerBlockEntity("trommel", TrommelBlockEntity::new, BlockInitialiser.TROMMEL);
        FLUID_DRAIN = registerBlockEntity("fluid_drain", FluidDrainBlockEntity::new, BlockInitialiser.FLUID_DRAIN);
        FLUID_PORT = registerBlockEntity("fluid_port", FluidPortBlockEntity::new, BlockInitialiser.FLUID_PORT);
        INTEGRATOR = registerBlockEntity("integrator_egg", IntegratorBlockEntity::new, BlockInitialiser.INTEGRATOR_EGG);
        HEATER = registerBlockEntity("heater", HeaterBlockEntity::new, BlockInitialiser.HEATER);
        SPIGOT = registerBlockEntity("spigot", SpigotBlockEntity::new, BlockInitialiser.SPIGOT);

        ItemStorage.SIDED.registerSelf(ITEM_BUFFER_BLOCK_ENTITY);
        ItemStorage.SIDED.registerSelf(TROMMEL_BLOCK_ENTITY);
        FluidStorage.SIDED.registerSelf(FLUID_PORT);

        FluidStorage.SIDED.registerFallback((world, pos, state, be, direction) ->
        {
            if (be instanceof FluidBuffer.FluidBufferProvider provider)
            {
                return (Storage<FluidVariant>) provider.getBuffer(direction);
            }
            return null;
        });
    }
}
