package com.neep.neepmeat.machine.synthesiser;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class MobSynthesisRegistry
{
    private static final Map<EntityType<?>, Entry> map = new HashMap<>();

    public static void register(EntityType<?> type, long meat, int time)
    {
        Identifier id = Registries.ENTITY_TYPE.getId(type);
//        if (id == null) throw new IllegalArgumentException("The given EntityType has not been registered");

        map.put(type, new Entry(type, id, meat, time));
    }

    public static Entry get(Entity entity)
    {
        return get(entity.getType());
    }

    public static Entry get(EntityType<?> type)
    {
        return map.get(type);
    }

    public static record Entry(EntityType<?> type, Identifier id, long meat, int time)
    {
    }

    public static long meatForEntity(Entity entity)
    {
        return (long) Math.floor(entity.getHeight() * entity.getWidth() * entity.getWidth());
    }

    public static void initDefaults()
    {
//        register(EntityType.PIG, FluidConstants.BUCKET, 60);
//        register(EntityType.COW, FluidConstants.BUCKET, 60);

        // Generate entries for all spawn eggs based on bounding box size
        SpawnEggItem.getAll().forEach(eggItem ->
        {
            EntityType<?> type = eggItem.getEntityType(null);
            register(type, (long) Math.floor(type.getHeight() * type.getWidth() * type.getWidth() * FluidConstants.BUCKET), 60);
        });
    }
}
