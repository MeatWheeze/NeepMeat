package com.neep.neepmeat.init;

import com.neep.meatlib.registry.EntityRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.EggEntity;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.entity.MobPlatformRidingEntity;
import com.neep.neepmeat.entity.TankMinecartEntity;
import com.neep.neepmeat.entity.worm.WormEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.FlyingEntity;

public class NMEntities
{
    public static EntityType<TankMinecartEntity> TANK_MINECART;
    public static EntityType<GlomeEntity> GLOME;
    public static EntityType<EggEntity> EGG;

    public static EntityType<WormEntity> WORM;
    public static EntityType<WormEntity.WormSegment> WORM_SEGMENT;
    public static EntityType<MobPlatformRidingEntity> MOB_PLATFORM;

    public static void initialise()
    {
        TANK_MINECART = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "tank_minecart", FabricEntityTypeBuilder.create(SpawnGroup.MISC, TankMinecartEntity::new)
                .dimensions(EntityDimensions.fixed(0.98f, 0.7f)).trackedUpdateRate(8).trackedUpdateRate(1).build());

        GLOME = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "glome", FabricEntityTypeBuilder.<GlomeEntity>create(SpawnGroup.MISC, GlomeEntity::new)
                .dimensions(EntityDimensions.fixed(0.7f, 0.7f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(GLOME, GlomeEntity.createLivingAttributes());

        EGG = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "mob_egg", FabricEntityTypeBuilder.<EggEntity>create(SpawnGroup.MISC, EggEntity::new)
                .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).trackedUpdateRate(8).trackedUpdateRate(1).build());

        WORM = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "god_worm", FabricEntityTypeBuilder.<WormEntity>create(SpawnGroup.MISC, WormEntity::new)
                .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(WORM, WormEntity.createLivingAttributes());
//        WORM_SEGMENT = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "god_worm_segment", FabricEntityTypeBuilder.<WormEntity>create(SpawnGroup.MISC, WormEntity.WormSegment::new)
//                .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
//        FabricDefaultAttributeRegistry.register(WORM, WormEntity.createLivingAttributes());

        MOB_PLATFORM = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "mob_platform", FabricEntityTypeBuilder.create(SpawnGroup.MISC, MobPlatformRidingEntity::new)
                .dimensions(EntityDimensions.fixed(0.1f, 0.1f)).trackedUpdateRate(1).build());
    }
}
