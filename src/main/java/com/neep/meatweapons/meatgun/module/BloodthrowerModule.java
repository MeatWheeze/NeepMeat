package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.meatweapons.particle.MuzzleFlashParticleType;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.neep.meatweapons.meatgun.module.BosherModule.getRayTargets;

public class BloodthrowerModule extends ShooterModule
{
    private final Random shotRandom = Random.create();

    public BloodthrowerModule(RootModuleHolder.Listener listener)
    {
        super(listener, 1, 2, AmmunitionType.BLOOD);
    }

    public BloodthrowerModule(RootModuleHolder.Listener listener, NbtCompound nbt)
    {
        this(listener);
        readNbt(nbt);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BLOODTHROWER;
    }

    @Override
    public void tick(PlayerEntity player)
    {
        cooldown = Math.max(0, cooldown - 1);
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType, Hand hand)
    {
        if (cooldown == 0)
        {
            if (consume(player.getInventory(), player))
            {
                cooldown = maxCooldown;
                fireBeam(world, player, stack, pitch, yaw);
            }
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType, Hand hand)
    {
        trigger(world, player, stack, id, pitch, yaw, handType, hand);
    }

    public Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack)
    {
        boolean sneak = entity.isSneaking();
        return new Vec3d(
                sneak ? 0 : entity.getMainHandStack().equals(stack) ? -0.13 : 0.13,
                0,
                .25);
    }

    @Override
    public int capacity()
    {
        return 64;
    }

    protected void fireBeam(World world, PlayerEntity player, ItemStack stack, double pitchd, double yawd)
    {
        Vec3d pos = player.getEyePos();
        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitchd).rotateY((float) -yawd);
        pos = pos.add(transform);

        List<Entity> targets = hitScan(player, pos, pitchd, yawd, 5, 40, 4, shotRandom);
        for (var target : targets)
        {
            if (target.isAlive())
            {
                target.damage(BulletDamageSource.create(world, player, 0.0f), 0.5f);
                target.timeUntilRegen = 0;
            }
        }

        world.playSoundFromEntity(null, player, NMSounds.BLOODTHROWER_ACTIVE, SoundCategory.PLAYERS, 1f, 1f);
        if (world instanceof ServerWorld serverWorld)
        {
            Vector4d v = new Vector4d(0, 0, -13 / 16f, 1);
            v.mul(this.transform);
            serverWorld.spawnParticles(
                    new MuzzleFlashParticleType.MuzzleFlashParticleEffect(MWParticles.BLOOD_MUZZLE_FLASH, player, v.x, v.y, v.z, 2.2f, 1)
                    , pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0.1);
        }
    }

    private static List<Entity> hitScan(@NotNull LivingEntity caster, Vec3d start, double pitch, double yaw, int numRays,
                                       double perturb, double distance, Random random)
    {
        perturb = Math.toRadians(perturb);

        // Modify the main ray to account for block collision
        Vec3d end = start.add(GunItem.getRotationVector(pitch, yaw).multiply(distance));
        RaycastContext ctx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
        BlockHitResult blockResult = caster.getWorld().raycast(ctx);
        if (blockResult.getType() == HitResult.Type.BLOCK)
        {
            end = blockResult.getPos();
            distance = end.distanceTo(start);
        }

        // Form list of vectors from perturbed pitch and yaw
        List<Vec3d> rays = new ArrayList<>(numRays);
        for (int i = 0; i < numRays; ++i)
        {
            double yaw1 = yaw + perturb * (random.nextFloat() - 0.5);
            double pitch1 = pitch + perturb * (random.nextFloat() - 0.5);

            Vec3d ray = GunItem.getRotationVector(pitch1, yaw1).multiply(distance);
            rays.add(ray);

            spawnParticle((ServerWorld) caster.getWorld(), start, ray.normalize());
        }


        Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.canHit();

        List<Entity> targets = new ArrayList<>();
        for (var hit : getRayTargets(caster, start, end, rays, distance, entityFilter, 0.1))
        {
            if (hit.getType() == HitResult.Type.ENTITY)
            {
                targets.add(((EntityHitResult) hit).getEntity());
            }
//            syncBeamEffect((ServerWorld) caster.getWorld(), start, hit.getPos(), 0.2f, 9, 50);
        }

        return targets;
    }

    private static void spawnParticle(ServerWorld world, Vec3d start, Vec3d ray)
    {
        world.spawnParticles(MWParticles.BLOODTHROWER_SPLASH, start.x, start.y, start.z, 0, ray.x, ray.y, ray.z, 0.8);
    }

    private static void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, float width, int maxTime, double showRadius)
    {
        Vec3d col = new Vec3d(214, 175, 32);
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BULLET_TRAIL, world, pos, end, col, 0.1f, 1);
        }
    }
}
