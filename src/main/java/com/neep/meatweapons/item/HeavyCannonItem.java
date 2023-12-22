package com.neep.meatweapons.item;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.CannonBulletEntity;
import com.neep.neepmeat.init.SoundInitialiser;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class HeavyCannonItem extends BaseGunItem implements IAnimatable
{
    public AnimationFactory factory = new AnimationFactory(this);
    public String controllerName = "controller";

    public HeavyCannonItem()
    {
        super("heavy_cannon", MeatWeapons.BALLISTIC_CARTRIDGE, 1, 30,false, new FabricItemSettings());
        this.sounds.put(GunSounds.FIRE_PRIMARY, SoundInitialiser.HAND_CANNON_FIRE);
        this.sounds.put(GunSounds.RELOAD, SoundInitialiser.HAND_CANNON_RELOAD);
    }

    @Override
    public void registerControllers(AnimationData animationData)
    {
        animationData.addAnimationController(new AnimationController(this, controllerName, 1, this::predicate));
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.NONE;
    }

    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
        fire(world, user, itemStack);
        return TypedActionResult.fail(itemStack);
    }

//    @Override
//    public Vec3f getAimOffset()
//    {
//        return new Vec3f(0.46f, 0, 0);
//    }
    @Override
    public Vec3d getMuzzleOffset(PlayerEntity player, ItemStack stack)
    {
        return new Vec3d(player.getMainHandStack().equals(stack) ? -0.2 : 0.2,
                player.isSneaking() ? -0.15 : 0.1,
                0);
    }

    public void fire(World world, PlayerEntity player, ItemStack stack)
    {
        if (!player.getItemCooldownManager().isCoolingDown(this))
        {
            if (stack.getDamage() != this.maxShots)
            {
                player.getItemCooldownManager().set(this, cooldown);

                if (!world.isClient)
                {
                    double yaw = Math.toRadians(player.getHeadYaw());
                    double pitch = Math.toRadians(player.getPitch(0.1f));

                    double mult = 5; // Multiplier for bullet speed.
                    double vx = mult * -Math.sin(yaw) * Math.cos(pitch) + player.getVelocity().getX();
                    double vy = mult * -Math.sin(pitch) + player.getVelocity().getY();
                    double vz = mult * Math.cos(yaw) * Math.cos(pitch) + player.getVelocity().getZ();

                    Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
                    if (!player.isSneaking())
                    {
                        Vec3d transform = getMuzzleOffset(player, stack).rotateY((float) -yaw);
                        pos = pos.add(transform);
                    }

                    CannonBulletEntity bullet = new CannonBulletEntity(world, pos.x, pos.y, pos.z, vx, vy, vz);
                    bullet.setOwner(player);
                    world.spawnEntity(bullet);

                    playSound(world, player, GunSounds.FIRE_PRIMARY);

                    stack.setDamage(stack.getDamage() + 1);

                    final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld) world);
                    GeckoLibNetwork.syncAnimation(player, this, id, ANIM_FIRE);
                    for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
                    {
                        GeckoLibNetwork.syncAnimation(otherPlayer, this, id, ANIM_FIRE);
                    }
                }
            } else // Weapon is out of ammunition.
            {
                if (world.isClient)
                {
                    // Play empty sound.
                }
                else
                {
                    this.reload(player, stack);
                }
            }
        }
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
//        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.blaster.fire", false));
        return PlayState.CONTINUE;
    }

    @Override
    public void onAnimationSync(int id, int state)
    {
        if (state == ANIM_FIRE)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.heavy_cannon.fire", false));
        }
        else if (state == ANIM_RELOAD)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.heavy_cannon.reload", false));
        }
    }
}
