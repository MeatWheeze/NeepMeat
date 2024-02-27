package com.neep.meatweapons.item;

import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.model.BaseGunModel;
import com.neep.meatweapons.entity.ExplodingShellEntity;
import com.neep.meatweapons.entity.WeaponCooldownAttachment;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.model.GeoModel;

public class MA75Item extends BaseGunItem implements WeakTwoHanded
{
    public String controllerName = "controller";

    public MA75Item()
    {
        super("ma75", MWItems.BALLISTIC_CARTRIDGE, 50, 1, false, new MeatlibItemSettings());
        this.sounds.put(GunSounds.FIRE_PRIMARY, NMSounds.AR_FIRE);
        this.sounds.put(GunSounds.FIRE_SECONDARY, NMSounds.HAND_CANNON_FIRE);
        this.sounds.put(GunSounds.RELOAD, NMSounds.HAND_CANNON_RELOAD);
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack)
    {
        return false;
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 99999;
    }

    @Override
    public Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack)
    {
        boolean sneak = entity.isSneaking();
        return new Vec3d(
                sneak ? 0 : entity.getMainHandStack().equals(stack) ? -0.2 : 0.2,
                sneak ? -0.25 : 0.1,
                .2);
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        WeaponCooldownAttachment manager = WeaponCooldownAttachment.get(player);

        if (stack.getDamage() >= stack.getMaxDamage())
        {
            reload(player, stack, null);
            return;
        }

        if (id == MWAttackC2SPacket.TRIGGER_PRIMARY
            && !manager.isCoolingDown(stack, 0)
            && stack.getDamage() < stack.getMaxDamage()
        )
        {
            fire(world, player, stack, pitch, yaw);
            manager.set(stack, 0, 2);
            if (!player.isCreative()) stack.setDamage(stack.getDamage() + 1);
        }
        if (id == MWAttackC2SPacket.TRIGGER_SECONDARY
                && !manager.isCoolingDown(stack, 1)
                && stack.getDamage() < stack.getMaxDamage()
        )
        {
            player.getItemCooldownManager().set(this, 3);
            fireShell(world, player, stack, 3, ((world1, x, y, z, vx, vy, vz) -> new ExplodingShellEntity(world, 1, false, x, y, z, vx, vy, vz)));
            manager.set(stack, 1, 15);
            if (!player.isCreative()) stack.setDamage(stack.getDamage() + 1);
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        trigger(world, player, stack, id, pitch, yaw, handType);
    }

    public void fire(World world, PlayerEntity player, ItemStack stack, double pitch, double yaw)
    {

        if (!world.isClient)
        {
            fireBeam(world, player, stack);
        }
    }

    @Override
    public int getShots(ItemStack stack, int trigger)
    {
        return switch (trigger)
        {
            case 1: yield maxShots - stack.getDamage();
            case 2: yield 10;
            default: yield -1;
        };
    }

    @Override
    protected GeoModel<? extends BaseGunItem> createModel()
    {
        return new BaseGunModel<>(
                new Identifier(MeatWeapons.NAMESPACE, "geo/ma75.geo.json"),
                new Identifier(MeatWeapons.NAMESPACE, "textures/general/ma75.png"),
                new Identifier(MeatWeapons.NAMESPACE, "animations/ma75.animation.json"));
    }

    @Override
    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BULLET_TRAIL, world, pos, end, velocity, 0.1f, 1);
        }
    }

//    @Override
//    public void onAnimationSync(int id, int state)
//    {
//        if (state == ANIM_FIRE)
//        {
//            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
//                controller.markNeedsReload();
//                controller.setAnimation(new AnimationBuilder().addAnimation("animation.ma75.fire"));
//        }
//        else if (state == ANIM_RELOAD)
//        {
//            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
//            controller.markNeedsReload();
//            controller.setAnimation(new AnimationBuilder().addAnimation("animation.machine_pistol.reload_r"));
//        }
//    }
}
