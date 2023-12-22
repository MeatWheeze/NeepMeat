package com.neep.neepmeat.init;

import com.neep.meatlib.registry.EntityRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.EggEntity;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.entity.TankMinecartEntity;
import com.neep.neepmeat.entity.keeper.KeeperEntity;
import com.neep.neepmeat.entity.worm.WormEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;

public class NMEntities
{
    public static EntityType<TankMinecartEntity> TANK_MINECART;
    public static EntityType<GlomeEntity> GLOME;
    public static EntityType<EggEntity> EGG;

    public static EntityType<WormEntity> WORM;
    public static EntityType<KeeperEntity> KEEPER;
    public static EntityType<WormEntity.WormSegment> WORM_SEGMENT;

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

        KEEPER = EntityRegistry.registerEntity(NeepMeat.NAMESPACE, "keeper", FabricEntityTypeBuilder.create(SpawnGroup.MISC, KeeperEntity::new)
                .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).trackedUpdateRate(8).trackedUpdateRate(1).build());
        FabricDefaultAttributeRegistry.register(KEEPER, KeeperEntity.createLivingAttributes());
    }
}
