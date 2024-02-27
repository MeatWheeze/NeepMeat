package com.neep.meatweapons;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.attachment.player.PlayerAttachmentManager;
import com.neep.meatweapons.enchantment.MWEnchantments;
import com.neep.meatweapons.entity.*;
import com.neep.meatweapons.item.AssaultDrillItem;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MeatWeapons implements ModInitializer
{
    public static final String NAMESPACE = "meatweapons";

    public static final ItemGroup WEAPONS = FabricItemGroup.builder().icon(() -> new ItemStack(NMItems.SLASHER)).build();

    public static EntityType<BulletEntity> BULLET;
    public static EntityType<CannonBulletEntity> CANNON_BULLET;
    public static EntityType<ZapProjectileEntity> ZAP;
    public static EntityType<FusionBlastEntity> FUSION_BLAST;
    public static EntityType<ExplodingShellEntity> EXPLODING_SHELL;
    public static EntityType<AirtruckEntity> AIRTRUCK;

    public static <T extends Entity> EntityType<T> registerEntity(String id, FabricEntityTypeBuilder<T> builder)
    {
        return Registry.register(
                Registries.ENTITY_TYPE, new Identifier(NAMESPACE, id),
                                   builder
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                        .trackRangeBlocks(4).trackedUpdateRate(10)
                        .build());
    }

    public static <T extends Entity> EntityType<T> registerEntity(String id, EntityType<T> type)
    {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(NAMESPACE, id), type);
    }

    @Override
    public void onInitialize()
    {
        try (var mcontext = MeatLib.getContext(NAMESPACE))
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

            new MWItems();
            MWParticles.init();
            MWGraphicsEffects.init();
            MWAttackC2SPacket.init();

            Registry.register(Registries.ITEM_GROUP, new Identifier(NAMESPACE, "weapons"), WEAPONS);

//        MWEnchantmentTargets.init();
            MWEnchantments.init();

            ProjectileSpawnPacket sp = new ProjectileSpawnPacket();

            PlayerAttachmentManager.registerAttachment(WeaponCooldownAttachment.ID, WeaponCooldownAttachment::new);

            FluidStorage.ITEM.registerForItems(AssaultDrillItem::getStorage, MWItems.ASSAULT_DRILL);
        }
    }

    public static boolean redirectClicks(ItemStack stack)
    {
        return stack.getItem() instanceof GunItem gun && gun.redirectClicks();
    }
}
