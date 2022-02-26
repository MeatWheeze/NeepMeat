package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.blockentity.*;
import com.neep.neepmeat.blockentity.integrator.IntegratorEggBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

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
    public static BlockEntityType<IntegratorEggBlockEntity> INTEGRATOR_EGG;

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
        INTEGRATOR_EGG = registerBlockEntity("integrator_egg", IntegratorEggBlockEntity::new, BlockInitialiser.INTEGRATOR_EGG);

        ItemStorage.SIDED.registerSelf(ITEM_BUFFER_BLOCK_ENTITY);
        ItemStorage.SIDED.registerSelf(TROMMEL_BLOCK_ENTITY);
        FluidStorage.SIDED.registerSelf(FLUID_PORT);

        FluidStorage.SIDED.registerFallback((world, pos, state, be, direction) ->
        {
            if (be instanceof FluidBufferProvider provider)
            {
                return provider.getBuffer(direction);
            }
            return null;
        });
    }
}
