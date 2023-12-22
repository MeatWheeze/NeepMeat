package com.neep.neepmeat.init;

import com.neep.meatlib.registry.EntityRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.TankMinecartEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class NMEntities
{
    public static EntityType<TankMinecartEntity> TANK_MINECART;

    public static void initialise()
    {
        TANK_MINECART = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "tank_minecart", FabricEntityTypeBuilder.create(SpawnGroup.MISC, TankMinecartEntity::new)
                .dimensions(EntityDimensions.fixed(0.98f, 0.7f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
    }
}
