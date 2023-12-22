package com.neep.meatweapons.item;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.Util;
import com.neep.meatweapons.entity.FusionBlastEntity;
import com.neep.meatweapons.entity.WeaponCooldownAttachment;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
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
        animationData.addAnimationController(new AnimationController<>(this, controllerName, 1, this::predicate));
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

    protected static final String KEY_CHARGE = "charge";

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        WeaponCooldownAttachment manager = WeaponCooldownAttachment.get(player);
        if (id == MWAttackC2SPacket.TRIGGER_PRIMARY)
        {
            if (!manager.isCoolingDown(stack))
            {
                if (stack.getDamage() != this.maxShots)
                {
                    // Primary trigger
                    manager.set(stack, cooldown);
                    fireBeam(world, player, stack, pitch, yaw);
                    if (!player.isCreative()) stack.setDamage(stack.getDamage() + 1);
                }
            }

        }
        else if (id == MWAttackC2SPacket.TRIGGER_SECONDARY)
        {
            if (stack.getDamage() + 2 > stack.getMaxDamage())
            {
                this.reload(player, stack, null);
                return;
            }

            // Start charging secondary attack
            NbtCompound nbt = stack.getOrCreateSubNbt(KEY_CHARGE);
            nbt.putInt("charge", 0);
            nbt.putBoolean("charging", true);

            world.playSoundFromEntity(null, player, NMSounds.FUSION_BLAST_CHARGE, SoundCategory.PLAYERS, 1f, 1f);
        }

        if (!manager.isCoolingDown(stack) && stack.getDamage() >= this.maxShots)
        {
            this.reload(player, stack, null);
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        // Continuously try to use the primary attack
        if (id == MWAttackC2SPacket.TRIGGER_PRIMARY)
        {
            trigger(world, player, stack, id, pitch, yaw, handType);
        }

        // Increase charge in the secondary attack
        if (id == MWAttackC2SPacket.TRIGGER_SECONDARY)
        {
            NbtCompound nbt = stack.getOrCreateSubNbt(KEY_CHARGE);
            if (nbt.getBoolean("charging"))
            {
                int charge = nbt.getInt("charge");
                nbt.putInt("charge", charge + 1);

                // Play a beep every second to indicate charge.
                if (charge != 0 && charge % 20 == 0)
                    world.playSoundFromEntity(null, player, NMSounds.BEEP, SoundCategory.PLAYERS, 1f, 1f + (float) charge / EXPLOSION_TIME);

                // Damage the player if they hold for too long.
                if (charge > EXPLOSION_TIME)
                {
                    world.createExplosion(player, player.getX(), player.getY(), player.getZ(), 1, Explosion.DestructionType.NONE);
                    world.playSoundFromEntity(null, player, NMSounds.FUSION_BLAST_FIRE, SoundCategory.PLAYERS, 1f, 1f);
                    nbt.putInt("charge", 0);
                    nbt.putBoolean("charging", false);
                }
            }
        }
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        // Check that the charge exceeds a threshold. If so, shoot the projectile.
        WeaponCooldownAttachment manager = WeaponCooldownAttachment.get(player);
        if (id == MWAttackC2SPacket.TRIGGER_SECONDARY)
        {
            if (stack.getDamage() + 2 <= maxShots
                    && canFireBlast(stack))
            {
                NbtCompound nbt = stack.getOrCreateSubNbt(KEY_CHARGE);
                int charge = nbt.getInt("charge");
                float power = charge / 2f;

                fireShell(world, player, stack, 2, (world1, x, y, z, vx, vy, vz) -> new FusionBlastEntity(world1, x, y, z, vx, vy, vz, power));
                if (!player.isCreative()) stack.setDamage(stack.getDamage() + 2);

                world.playSoundFromEntity(null, player, NMSounds.FUSION_BLAST_FIRE, SoundCategory.PLAYERS, 1f, 1f);

                nbt.putBoolean("charging", false);
            }
            else
            {
                world.playSoundFromEntity(null, player, NMSounds.CLICK, SoundCategory.PLAYERS, 1f, 1f);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public static final int MAX_CHARGE_TIME = 60; // 60 ticks
    public static final int MIN_CHARGE_TIME = 13;
    public static final int EXPLOSION_TIME = 150; // Number of ticks before the weapon explodes when charging

    protected static boolean canFireBlast(ItemStack stack)
    {
        return stack.getOrCreateSubNbt(KEY_CHARGE).getInt("charge") > MIN_CHARGE_TIME;
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

    protected void fireBeam(World world, PlayerEntity player, ItemStack stack, double pitch, double yaw)
    {
        // Get projectile starting position and direction.
        Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitch).rotateY((float) -yaw);
        pos = pos.add(transform);

        Vec3d end = pos.add(Util.getRotationVector((float) pitch, (float) yaw).multiply(20));
        Optional<Entity> target = hitScan(player, pos, end, 20, this);
        target.ifPresent(livingEntity -> livingEntity.damage(DamageSource.player(player), 4));

        // Play fire sound
        playSound(world, player, GunSounds.FIRE_PRIMARY);


        syncAnimation(world, player, stack, ANIM_FIRE, true);
    }

    @Override
    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BEAM, world, pos, end, velocity, 0.5f, 5);
//            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet);
        }
    }

    @Override
    public void onAnimationSync(int id, int state)
    {
        final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
        if (state == ANIM_FIRE)
        {
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.fusion.fire"));
        }
        else if (state == ANIM_RELOAD)
        {
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.fusion.reload_r"));
        }
    }
}
