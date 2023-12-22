package com.neep.neepmeat.init;

import com.neep.meatlib.registry.EntityRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.EggEntity;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.entity.TankMinecartEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.FlyingEntity;

public class NMEntities
{
    public static EntityType<TankMinecartEntity> TANK_MINECART;
    public static EntityType<GlomeEntity> GLOME;
    public static EntityType<EggEntity> EGG;

    public static void initialise()
    {
        TANK_MINECART = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "tank_minecart", FabricEntityTypeBuilder.create(SpawnGroup.MISC, TankMinecartEntity::new)
                .dimensions(EntityDimensions.fixed(0.98f, 0.7f)).trackedUpdateRate(8).trackedUpdateRate(1).build());

        GLOME = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "glome", FabricEntityTypeBuilder.<GlomeEntity>create(SpawnGroup.MISC, GlomeEntity::new)
                .dimensions(EntityDimensions.fixed(0.7f, 0.7f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(GLOME, GlomeEntity.createLivingAttributes());

        EGG = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "mob_egg", FabricEntityTypeBuilder.<EggEntity>create(SpawnGroup.MISC, EggEntity::new)
                .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
    }
}
