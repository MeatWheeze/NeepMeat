package com.neep.neepmeat.init;

import com.neep.meatlib.registry.EntityRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.EggEntity;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.entity.LimbEntity;
import com.neep.neepmeat.entity.bovine_horror.AcidSprayEntity;
import com.neep.neepmeat.entity.bovine_horror.BovineHorrorEntity;
import com.neep.neepmeat.entity.hound.HoundEntity;
import com.neep.neepmeat.entity.keeper.KeeperEntity;
import com.neep.neepmeat.entity.worm.WormEntity;
import com.neep.neepmeat.machine.phage_ray.PhageRayBlockEntity;
import com.neep.neepmeat.machine.phage_ray.PhageRayEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class NMEntities
{
    public static EntityType<HoundEntity> HOUND;
    public static EntityType<GlomeEntity> GLOME;
    public static EntityType<EggEntity> EGG;

    public static EntityType<WormEntity> WORM;
    public static EntityType<KeeperEntity> KEEPER;
    public static EntityType<BovineHorrorEntity> BOVINE_HORROR;
    public static EntityType<AcidSprayEntity> ACID_SPRAY;
    public static EntityType<WormEntity.WormSegment> WORM_SEGMENT;
    public static EntityType<LimbEntity> LIMB;

    public static EntityType<PhageRayEntity> PHAGE_RAY;

    public static void initialise()
    {
//        TANK_MINECART = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "tank_minecart", FabricEntityTypeBuilder.create(SpawnGroup.MISC, TankMinecartEntity::new)
//                .dimensions(EntityDimensions.fixed(0.98f, 0.7f)).trackedUpdateRate(8).trackedUpdateRate(1).build());

        GLOME = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "glome", FabricEntityTypeBuilder.<GlomeEntity>create(SpawnGroup.MISC, GlomeEntity::new)
                .dimensions(EntityDimensions.fixed(0.7f, 0.7f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(GLOME, GlomeEntity.createLivingAttributes());

        EGG = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "mob_egg", FabricEntityTypeBuilder.<EggEntity>create(SpawnGroup.MISC, EggEntity::new)
                .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).trackedUpdateRate(8).trackedUpdateRate(1).build());

        WORM = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "god_worm", FabricEntityTypeBuilder.<WormEntity>create(SpawnGroup.MISC, WormEntity::new)
                .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(WORM, WormEntity.createLivingAttributes());

        KEEPER = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "keeper", FabricEntityTypeBuilder.create(SpawnGroup.MISC, KeeperEntity::new)
                .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(KEEPER, KeeperEntity.createLivingAttributes());

        HOUND = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "hound", FabricEntityTypeBuilder.create(SpawnGroup.MISC, HoundEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 2f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(HOUND, HoundEntity.createLivingAttributes());

        BOVINE_HORROR = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "bovine_horror", FabricEntityTypeBuilder.create(SpawnGroup.MISC, BovineHorrorEntity::new)
//                .dimensions(EntityDimensions.fixed(2.7f, 3.5f)).trackedUpdateRate(1).build());
                .dimensions(EntityDimensions.fixed(2.7f, 3.5f)).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(BOVINE_HORROR, BovineHorrorEntity.createLivingAttributes());
        ACID_SPRAY = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "acid_spray", FabricEntityTypeBuilder.create(SpawnGroup.MISC, AcidSprayEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).trackedUpdateRate(8).build());

        PHAGE_RAY = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "phage_ray", FabricEntityTypeBuilder.create(SpawnGroup.MISC, PhageRayEntity::new)
                .dimensions(EntityDimensions.fixed(2.8f, 2.8f)).trackedUpdateRate(3).build());

        LIMB = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "limb", FabricEntityTypeBuilder.create(SpawnGroup.MISC, LimbEntity::new)
                .dimensions(EntityDimensions.fixed(0.5f, 0.6f)).trackedUpdateRate(3).build());
    }
}
