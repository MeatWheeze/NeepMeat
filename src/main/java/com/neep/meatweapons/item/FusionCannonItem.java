package com.neep.meatweapons.item;

import com.neep.meatweapons.entity.PlasmaProjectileEntity;
import com.neep.neepmeat.init.SoundInitialiser;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
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

public class FusionCannonItem extends BaseGunItem implements IAnimatable, WeakTwoHanded
{
    public AnimationFactory factory = new AnimationFactory(this);
    public String controllerName = "controller1";

    public FusionCannonItem()
    {
        super("fusion", Items.DIRT, 16, 10, false, new FabricItemSettings());
        this.sounds.put(GunSounds.FIRE_PRIMARY, SoundInitialiser.FUSION_FIRE);
        this.sounds.put(GunSounds.RELOAD, SoundInitialiser.RELOAD);
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

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 70000;
    }
    @Override
    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
//        user.setCurrentHand(hand);
        fire(world, user, user.getStackInHand(hand));

        ItemStack stack = user.getStackInHand(hand);
        if (!user.getItemCooldownManager().isCoolingDown(this) && stack.getDamage() == this.maxShots)
        {
            this.reload(user, stack);
            return TypedActionResult.pass(stack);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        ItemStack itemStack = user.getStackInHand(Hand.MAIN_HAND);
        if (user instanceof PlayerEntity)
        {
            fire(world, (PlayerEntity) user, itemStack);
        }
    }

    @Override
    public Vec3f getAimOffset()
    {
        return new Vec3f(0.56f, 0, 0);
    }

    public void fire(World world, PlayerEntity player, ItemStack stack)
    {
        {
            if (!player.getItemCooldownManager().isCoolingDown(this))
            {
                if (stack.getDamage() != this.maxShots)
                {
                    player.getItemCooldownManager().set(this, cooldown);

                    if (!world.isClient)
                    {
                        double yaw = Math.toRadians(player.getHeadYaw()) + 0.1 * (rand.nextFloat() - 0.5);
                        double pitch = Math.toRadians(player.getPitch(0.1f)) + 0.1 * (rand.nextFloat() - 0.5);

                        // Convert pitch and yaw to look vector.
                        double mult = 5; // Multiplier for bullet speed.
                        double vx = mult * -Math.sin(yaw) * Math.cos(pitch) + player.getVelocity().getX();
                        double vy = mult * -Math.sin(pitch) + player.getVelocity().getY();
                        double vz = mult * Math.cos(yaw) * Math.cos(pitch) + player.getVelocity().getZ();

                        // Get projectile starting position and direction.
                        Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
                        if (!player.isSneaking())
                        {
                            Vec3d transform = new Vec3d(
                                    player.getMainHandStack().equals(stack) ? -0.2 : 0.2,
                                    player.isSneaking() ? -0.15 : 0.1,
                                    0).rotateY((float) -yaw);
                            pos = pos.add(transform);
                        }

                        // Create projectile.
                        PlasmaProjectileEntity bullet = new PlasmaProjectileEntity(world, pos.x, pos.y, pos.z, vx, vy, vz);
                        bullet.setOwner(player);
                        world.spawnEntity(bullet);

                        // Play fire sound
                        playSound(world, player, GunSounds.FIRE_PRIMARY);

                        if (!player.isCreative())
                        {
                            stack.setDamage(stack.getDamage() + 1);
                        }

                        final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld) world);
                        GeckoLibNetwork.syncAnimation(player, this, id, ANIM_FIRE);
                        for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
                        {
                            GeckoLibNetwork.syncAnimation(otherPlayer, this, id, ANIM_FIRE);
                        }
                    }
                }
            }
        }
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        return PlayState.CONTINUE;
    }

    @Override
    public void onAnimationSync(int id, int state)
    {
        if (state == ANIM_FIRE)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.fusion.fire", false));
        }
        else if (state == ANIM_RELOAD)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.fusion.reload_r", false));
        }
    }
}
