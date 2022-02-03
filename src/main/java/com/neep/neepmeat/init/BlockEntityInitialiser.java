package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.blockentity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockEntityInitialiser
{
    public static BlockEntityType<?> TEST_BLOCK_ENTITY;
    public static BlockEntityType<PumpBlockEntity> PUMP_BLOCK_ENTITY;
    public static BlockEntityType<TankBlockEntity> TANK_BLOCK_ENTITY;
    public static BlockEntityType<GlassTankBlockEntity> GLASS_TANK_BLOCK_ENTITY;


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

        TEST_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                NeepMeat.NAMESPACE + "test_block_entity",
                FabricBlockEntityTypeBuilder.create(TestBlockEntity::new, (Block) BlockInitialiser.TANK)
                        .build());

//        FluidStorage.SIDED.registerSelf(PUMP_BLOCK_ENTITY);

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
