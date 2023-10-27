package com.neep.meatlib.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityRegistry
{
    public static <T extends Entity> EntityType<T> registerEntity(String namespace, String id, EntityType<?> type)
    {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(namespace, id), (EntityType<T>) type);
//                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
//                        .trackRangeBlocks(4).trackedUpdateRate(10)
//                        .build());
    }
}
