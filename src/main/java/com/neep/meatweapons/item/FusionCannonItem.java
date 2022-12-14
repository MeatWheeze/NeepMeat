package com.neep.meatweapons.item;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.init.GraphicsEffects;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.meatweapons.particle.GraphicsEffect;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
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

import java.util.Optional;

public class FusionCannonItem extends BaseGunItem implements IAnimatable, IWeakTwoHanded, IAimable
{
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public String controllerName = "controller1";

    public FusionCannonItem()
    {
        super("fusion", MWItems.BALLISTIC_CARTRIDGE, 16, 10, false, new FabricItemSettings());
        this.sounds.put(GunSounds.FIRE_PRIMARY, NMSounds.FUSION_FIRE);
        this.sounds.put(GunSounds.RELOAD, NMSounds.RELOAD);
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
        fire(world, user, user.getStackInHand(hand));

        ItemStack stack = user.getStackInHand(hand);
        if (!user.getItemCooldownManager().isCoolingDown(this) && stack.getDamage() == this.maxShots)
        {
            this.reload(user, stack, null);
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

    @Override
    public Vec3d getMuzzleOffset(PlayerEntity player, ItemStack stack)
    {
        boolean sneak = player.isSneaking();
        return new Vec3d(
                sneak ? 0 : player.getMainHandStack().equals(stack) == (player.getMainArm() == Arm.RIGHT) ? -0.2 : 0.2,
                sneak ? -0.25 : 0.1,
                .5);
    }

    @Override
    public void fire(World world, PlayerEntity player, ItemStack stack)
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

                    // Get projectile starting position and direction.
                    Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
                    Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitch).rotateY((float) -yaw);
                    pos = pos.add(transform);

                    Vec3d end = pos.add(player.getRotationVec(0.5f).multiply(20));
                    Optional<Entity> target = this.hitScan(player, pos, end, 20);
                    target.ifPresent(livingEntity -> livingEntity.damage(DamageSource.player(player), 4));

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

    @Override
    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, GraphicsEffect.Factory type, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            Packet<?> packet = BeamPacket.create(world, GraphicsEffects.BEAM, pos, end, velocity, 0.5f, 5, MWNetwork.EFFECT_ID);
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
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.fusion.fire"));
        }
        else if (state == ANIM_RELOAD)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.fusion.reload_r"));
        }
    }

    public static float transformWeapon(LivingEntity entity, ItemStack itemStack, boolean isAiming, float itemXOffset)
    {
        boolean left = entity.getMainArm() == Arm.RIGHT;
        if (itemStack.getItem() instanceof BaseGunItem && isAiming)
        {
            itemXOffset = (float) MathHelper.lerp(0.3, itemXOffset, (left ? -1 : 1) * -0.34);
        }
        else
        {
            itemXOffset = (float) MathHelper.lerp(0.3, itemXOffset, 0);
        }
        return 0;
    }
}
