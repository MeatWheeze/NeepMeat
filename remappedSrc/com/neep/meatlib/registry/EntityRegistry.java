package com.neep.meatlib.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityRegistry
{
    public static <T extends Entity> EntityType<T> registerEntity(String namespace, String id, EntityType<?> type)
    {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(namespace, id), (EntityType<T>) type);
//                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
//                        .trackRangeBlocks(4).trackedUpdateRate(10)
//                        .build());
    }
}
