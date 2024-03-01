package com.neep.meatweapons.item;

import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.client.model.LMGItemModel;
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
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.model.GeoModel;

public class LMGItem extends BaseGunItem
{
    public LMGItem()
    {
        super("light_machine_gun", MWItems.BALLISTIC_CARTRIDGE, 50, 1, false, new MeatlibItemSettings());
        this.sounds.put(GunSounds.FIRE_PRIMARY, NMSounds.LMG_FIRE);
        this.sounds.put(GunSounds.RELOAD, NMSounds.HAND_CANNON_RELOAD);
    }

    @Override
    protected GeoModel<? extends BaseGunItem> createModel()
    {
        return new LMGItemModel();
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 99999;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        ItemStack itemStack = user.getStackInHand(Hand.MAIN_HAND);
        trigger(world, (PlayerEntity) user, itemStack, 0, user.getPitch(), user.getYaw(), MWAttackC2SPacket.HandType.MAIN);
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
        if (!player.getItemCooldownManager().isCoolingDown(this))
        {
            if (stack.getDamage() != this.maxShots)
            {
                player.getItemCooldownManager().set(this, 2);

                if (!world.isClient)
                {
                    fireBeam(world, player, stack);
                    if (!player.isCreative())
                        stack.setDamage(stack.getDamage() + 1);
                }
            }
            else // Weapon is out of ammunition.
            {
                if (world.isClient)
                {
                    // Play empty sound.
                }
                else
                {
                    // Try to reload
                    this.reload(player, stack, null);
                }
            }
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        trigger(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BULLET_TRAIL, world, pos, end, velocity, 0.1f, 1);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, controllerName, 0, this::fireController)
                .triggerableAnim("fire", RawAnimation.begin().thenPlay("animation.light_machine_gun.fire")));
    }
}
