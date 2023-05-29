package com.neep.meatweapons;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.attachment.player.PlayerAttachmentManager;
import com.neep.meatweapons.enchantment.MWEnchantmentTargets;
import com.neep.meatweapons.enchantment.MWEnchantments;
import com.neep.meatweapons.entity.*;
import com.neep.meatweapons.item.IGunItem;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MeatWeapons implements ModInitializer
{
    public static final String NAMESPACE = "meatweapons";

    public static final ItemGroup WEAPONS = FabricItemGroupBuilder.build(
            new Identifier(NeepMeat.NAMESPACE, "weapons"),
            () -> new ItemStack(NMItems.SLASHER));

    public static EntityType<PlasmaProjectileEntity> PLASMA = registerEntity("plasma_projectile", FabricEntityTypeBuilder.create(SpawnGroup.MISC, PlasmaProjectileEntity::new));
    public static EntityType<BulletEntity> BULLET;
    public static EntityType<CannonBulletEntity> CANNON_BULLET;
    public static EntityType<ZapProjectileEntity> ZAP;
    public static EntityType<FusionBlastEntity> FUSION_BLAST;
    public static EntityType<ExplodingShellEntity> EXPLODING_SHELL;
    public static EntityType<AirtruckEntity> AIRTRUCK;

    public static <T extends Entity> EntityType<T> registerEntity(String id, FabricEntityTypeBuilder<T> builder)
    {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(NAMESPACE, id),
                builder
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                        .trackRangeBlocks(4).trackedUpdateRate(10)
                        .build());
    }

    public static <T extends Entity> EntityType<T> registerEntity(String id, EntityType<T> type)
    {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(NAMESPACE, id), type);
    }

    @Override
    public void onInitialize()
    {
        BULLET = registerEntity("bullet", FabricEntityTypeBuilder.create(SpawnGroup.MISC, BulletEntity::new));
        CANNON_BULLET = registerEntity("cannon_bullet", FabricEntityTypeBuilder.create(SpawnGroup.MISC, CannonBulletEntity::new));
        ZAP = registerEntity("zap", FabricEntityTypeBuilder.create(SpawnGroup.MISC, ZapProjectileEntity::new));
        FUSION_BLAST = registerEntity("fusion_blast", FabricEntityTypeBuilder.create(SpawnGroup.MISC, FusionBlastEntity::new));
        EXPLODING_SHELL = registerEntity("exploding_shell", FabricEntityTypeBuilder.create(SpawnGroup.MISC, ExplodingShellEntity::new));

        AIRTRUCK = registerEntity("airtruck", FabricEntityTypeBuilder.create(SpawnGroup.MISC, AirtruckEntity::new)
                .trackedUpdateRate(1)
                .forceTrackedVelocityUpdates(true)
                .dimensions(EntityDimensions.fixed(3F, 2.2F))
                .trackRangeBlocks(40)
                .build());

        MeatLib.setNamespace(NAMESPACE);
        new MWItems();
        MeatLib.flush();
        MWParticles.init();
        MWGraphicsEffects.init();
        MWAttackC2SPacket.init();

        MWEnchantmentTargets.init();
        MWEnchantments.init();

        ProjectileSpawnPacket sp = new ProjectileSpawnPacket();

        PlayerAttachmentManager.registerAttachment(WeaponCooldownAttachment.ID, WeaponCooldownAttachment::new);
    }

    public static boolean redirectClicks(ItemStack stack)
    {
        return stack.getItem() instanceof IGunItem gun && gun.redirectClicks();
    }

    public static IGunItem getGun(ItemStack stack)
    {
        return stack.getItem() instanceof IGunItem gun ? gun : null;
    }
}
