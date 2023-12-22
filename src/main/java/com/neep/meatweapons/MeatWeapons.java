package com.neep.meatweapons;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.entity.BulletEntity;
import com.neep.meatweapons.entity.CannonBulletEntity;
import com.neep.meatweapons.entity.PlasmaProjectileEntity;
import com.neep.meatweapons.item.FusionCannonItem;
import com.neep.meatweapons.item.HandCannonItem;
import com.neep.meatweapons.item.LMGItem;
import com.neep.meatweapons.item.MachinePistolItem;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.NMItemGroups;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MeatWeapons implements ModInitializer
{
    public static final String NAMESPACE = "meatweapons";

    public static EntityType<PlasmaProjectileEntity> PLASMA = registerEntity("plasma_projectile", FabricEntityTypeBuilder.create(SpawnGroup.MISC, PlasmaProjectileEntity::new));
    public static EntityType<BulletEntity> BULLET;
    public static EntityType<CannonBulletEntity> CANNON_BULLET;

    public static Item BALLISTIC_CARTRIDGE = new BaseCraftingItem("ballistic_cartridge", true, new FabricItemSettings().group(NMItemGroups.WEAPONS));
    public static Item FUSION_CANNON = new FusionCannonItem();
    public static Item HAND_CANNON = new HandCannonItem();
    public static Item MACHINE_PISTOL = new MachinePistolItem();
    public static Item LMG = new LMGItem();

    public static <T extends Entity> EntityType<T> registerEntity(String id, FabricEntityTypeBuilder<T> builder)
    {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(NAMESPACE, id),
                builder
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                        .trackRangeBlocks(4).trackedUpdateRate(10)
                        .build());
    }

    @Override
    public void onInitialize()
    {
        BULLET = registerEntity("bullet", FabricEntityTypeBuilder.create(SpawnGroup.MISC, BulletEntity::new));
        CANNON_BULLET = registerEntity("cannon_bullet", FabricEntityTypeBuilder.create(SpawnGroup.MISC, CannonBulletEntity::new));

        System.out.println(ItemRegistry.ITEMS);
        MeatLib.setNamespace(NAMESPACE);
        ItemRegistry.registerItems();
        MWParticles.init();
    }
}
