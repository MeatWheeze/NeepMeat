package com.neep.meatweapons.item;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import com.neep.meatweapons.particle.BeamEffect;
import com.neep.meatweapons.particle.GraphicsEffect;
import com.neep.neepmeat.init.SoundInitialiser;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
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

public class MachinePistolItem extends BaseGunItem implements IAnimatable
{
    public AnimationFactory factory = new AnimationFactory(this);
    public String controllerName = "controller";

    public MachinePistolItem()
    {
        super("machine_pistol", Items.DIRT, 24, 10, false, new FabricItemSettings());
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

        if (world.isClient)
        {
//            GraphicsEffect.addEffect(new BeamEffect((ClientWorld) user.world, user.getPos(), user.getPos().add(0, 3, 0), 40));
        }

        return TypedActionResult.fail(itemStack);
    }

    @Override
    public Vec3f getAimOffset()
    {
        return new Vec3f(0.0f, 0, 0);
    }

    public void fire(World world, PlayerEntity player, ItemStack stack)
    {
        {
            if (!player.getItemCooldownManager().isCoolingDown(this))
            {
                if (stack.getDamage() != this.maxShots)
                {
//                    player.getItemCooldownManager().set(this, 1);

                    if (!world.isClient)
                    {

                        Optional<LivingEntity> target = this.hitScan(player, 100,0.5f);
                        if (target.isPresent())
                        {
//                            target.get().maxHurtTime = 1;
//                            target.get().hurtTime = 20;
                            LivingEntity entity = target.get();
                            target.get().damage(DamageSource.player(player), 2);
//                            entity.hurtTime = entity.maxHurtTime = 0;
                            entity.timeUntilRegen = 0;
                        }

                        playSound(world, player, GunSounds.FIRE_PRIMARY);
                        stack.setDamage(stack.getDamage() + 1);

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
                        this.reload(player, stack);
                    }
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
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.machine_pistol.fire", false));
        }
        else if (state == ANIM_RELOAD)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.machine_pistol.reload_r", false));
        }
    }
}
