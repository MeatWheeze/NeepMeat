package com.neep.meatweapons.item;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.meatweapons.init.GraphicsEffects;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.meatweapons.particle.GraphicsEffect;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
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

import java.util.Optional;

public class LMGItem extends BaseGunItem implements IAnimatable
{
    public AnimationFactory factory = new AnimationFactory(this);
    public String controllerName = "controller";

    public LMGItem()
    {
        super("light_machine_gun", MWItems.BALLISTIC_CARTRIDGE, 50, 1, false, new FabricItemSettings());
        this.sounds.put(GunSounds.FIRE_PRIMARY, NMSounds.LMG_FIRE);
        this.sounds.put(GunSounds.RELOAD, NMSounds.HAND_CANNON_RELOAD);
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
    public int getMaxUseTime(ItemStack stack)
    {
        return 99999;
    }

    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        user.setCurrentHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.consume(itemStack);
//        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        ItemStack itemStack = user.getStackInHand(Hand.MAIN_HAND);
        fire(world, (PlayerEntity) user, itemStack);
    }

//    @Override
//    public Vec3f getAimOffset()
//    {
//        return new Vec3f(0.0f, 0, 0);
//    }

    @Override
    public Vec3d getMuzzleOffset(PlayerEntity player, ItemStack stack)
    {
        boolean sneak = player.isSneaking();
        return new Vec3d(
                sneak ? 0 : player.getMainHandStack().equals(stack) ? -0.2 : 0.2,
                sneak ? -0.25 : 0.1,
                .2);
    }

    @Override
    public void fire(World world, PlayerEntity player, ItemStack stack)
    {
        {
            if (!player.getItemCooldownManager().isCoolingDown(this))
            {
                if (stack.getDamage() != this.maxShots)
                {
                    player.getItemCooldownManager().set(this, 2);

                    if (!world.isClient)
                    {
                        double yaw = Math.toRadians(player.getHeadYaw()) + 0.1 * (rand.nextFloat() - 0.5);
                        double pitch = Math.toRadians(player.getPitch(0.1f)) + 0.1 * (rand.nextFloat() - 0.5);

                        Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
                        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitch).rotateY((float) -yaw);
                        pos = pos.add(transform);

                        double d = 0.2;
                        Vec3d perturb = new Vec3d(rand.nextFloat() - 0.5, rand.nextFloat() - 0.5, rand.nextFloat() - 0.5)
                                .multiply(d);
                        Vec3d end = pos.add(player.getRotationVec(1)
                                .add(perturb)
                                .multiply(40));
                        Optional<LivingEntity> target = this.hitScan(player, pos, end, 40);
                        if (target.isPresent())
                        {
                            LivingEntity entity = target.get();
                            target.get().damage(BulletDamageSource.create(player, 0.1f), 1);
                            entity.timeUntilRegen = 0;
                        }

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
                else // Weapon is out of ammunition.
                {
                    if (world.isClient)
                    {
                        // Play empty sound.
                    } else
                    {
                        // Try to reload
                        this.reload(player, stack, null);
                    }
                }
            }
        }
    }

    @Override
    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, GraphicsEffect.Factory type, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            Packet<?> packet = BeamPacket.create(world, GraphicsEffects.BULLET_TRAIL, pos, end, velocity, 0.1f, 1, MWNetwork.EFFECT_ID);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet);
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
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.light_machine_gun.fire", false));
        }
        else if (state == ANIM_RELOAD)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.machine_pistol.reload_r", false));
        }
    }
}
