package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.blockentity.PumpBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockEntityInitialiser
{
    public static BlockEntityType<PumpBlockEntity> PUMP_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
    NeepMeat.NAMESPACE + "pump_block_entity",
        FabricBlockEntityTypeBuilder.create(PumpBlockEntity::new, BlockInitialiser.PUMP)
        .build());
//
    public static void initialiseBlockEntities()
    {
    }
}
