package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.blockentity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
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
    public static BlockEntityType<TrommelBlockEntity> FLUID_DRAIN;


    public static void initialiseBlockEntities()
    {
        PUMP_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "pump_block_entity",
                FabricBlockEntityTypeBuilder.create(PumpBlockEntity::new, BlockInitialiser.PUMP)
                        .build());

        TANK_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "tank_block_entity",
                FabricBlockEntityTypeBuilder.create(TankBlockEntity::new, BlockInitialiser.TANK)
                        .build());

        GLASS_TANK_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "glass_tank_block_entity",
                FabricBlockEntityTypeBuilder.create(GlassTankBlockEntity::new, BlockInitialiser.GLASS_TANK)
                        .build());

        NODE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "test_block_entity",
                FabricBlockEntityTypeBuilder.create(NodeContainerBlockEntity::new, BlockInitialiser.PIPE)
                        .build());

        ITEM_DUCT_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "item_duct_block_entity",
                FabricBlockEntityTypeBuilder.create(ItemDuctBlockEntity::new, BlockInitialiser.ITEM_DUCT)
                        .build());

        ITEM_BUFFER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "item_buffer_block_entity",
                FabricBlockEntityTypeBuilder.create(ItemBufferBlockEntity::new, BlockInitialiser.ITEM_BUFFER)
                        .build());

        TROMMEL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "trommel_block_entity",
                FabricBlockEntityTypeBuilder.create(TrommelBlockEntity::new, BlockInitialiser.TROMMEL)
                        .build());

        FLUID_DRAIN = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "fluid_drain_block_entity",
                FabricBlockEntityTypeBuilder.create(TrommelBlockEntity::new, BlockInitialiser.TROMMEL)
                        .build());

        ItemStorage.SIDED.registerSelf(ITEM_BUFFER_BLOCK_ENTITY);
        ItemStorage.SIDED.registerSelf(TROMMEL_BLOCK_ENTITY);

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
