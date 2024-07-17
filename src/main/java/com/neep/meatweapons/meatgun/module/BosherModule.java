package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.meatweapons.item.BeamEffectProvider;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BosherModule extends ShooterModule
{
    private final Random random = Random.create();

    public BosherModule(RootModuleHolder.Listener listener)
    {
        super(listener, 1, 20);
        shotsRemaining = maxShots;
    }

    public BosherModule(RootModuleHolder.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public List<ModuleSlot> getChildren()
    {
        return List.of();
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BOSHER;
    }

    @Override
    public void tick(PlayerEntity player)
    {
        cooldown = Math.max(0, cooldown - 1);
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        if (id == 1)
        {
            if (shotsRemaining >= 0 && cooldown == 0)
            {
                cooldown = maxCooldown;

                if (!world.isClient)
                {
                    fireBeam(world, player, stack, pitch, yaw);
//                    if (!player.isCreative())
//                        stack.setDamage(stack.getDamage() + 1);
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
//                    this.reload(player, stack, null);
                }
            }
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        trigger(world, player, stack, id, pitch, yaw, handType);
    }

    public Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack)
    {
        boolean sneak = entity.isSneaking();
        return new Vec3d(
                sneak ? 0 : entity.getMainHandStack().equals(stack) ? -0.13 : 0.13,
                -0.04,
                .25);
    }

    protected void fireBeam(World world, PlayerEntity player, ItemStack stack, double pitchd, double yawd)
    {
        Vec3d pos = player.getEyePos();
        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitchd).rotateY((float) -yawd);
        pos = pos.add(transform);

        List<Entity> targets = hitScan(player, pos, pitchd, yawd, 5, 3.5,
                40, random, this::syncBeamEffect);
        for (var target : targets)
        {
            if (target.isAlive())
            {
                target.damage(BulletDamageSource.create(world, player, 0.1f), 4);
                target.timeUntilRegen = 0;
            }
        }

        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 7, 0.4f,0.3f, 0.01f);
        world.playSoundFromEntity(null, player, NMSounds.BOSHER_FIRE, SoundCategory.PLAYERS, 1f, 1f);
        if (world instanceof ServerWorld serverWorld)
        {
            Vector4d v = new Vector4d(0, 0, -13 / 16f, 1);
            v.mul(this.transform);
            serverWorld.spawnParticles(
                    new MuzzleFlashParticleType.MuzzleFlashParticleEffect(MWParticles.BOSHER_MUZZLE_FLASH, player, v.x, v.y, v.z, 2.2f, 1)
                    , pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0.1);
        }
    }

    public static List<Entity> hitScan(@NotNull LivingEntity caster, Vec3d start, double pitch, double yaw, int numRays,
                                           double perturb, double distance, Random random, BeamEffectProvider gunItem)
    {
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
            double yaw1 = yaw + perturb * 0.1 * (random.nextFloat() - 0.5);
            double pitch1 = pitch + perturb * 0.1 * (random.nextFloat() - 0.5);

            Vec3d ray = GunItem.getRotationVector(pitch1, yaw1).multiply(distance);
            rays.add(ray);
        }

        // Use the original ray to find the distance before block collision
        Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.canHit();

        List<Entity> targets = new ArrayList<>();
        for (var hit : getRayTargets(caster, start, end, rays, distance, entityFilter, 0.1))
        {
            if (hit.getType() == HitResult.Type.ENTITY)
            {
                targets.add(((EntityHitResult) hit).getEntity());
            }

            gunItem.syncBeamEffect((ServerWorld) caster.getWorld(), start, hit.getPos(), 0.2f, 9, 50);
        }

        return targets;
    }

    public static List<HitResult> getRayTargets(LivingEntity caster, Vec3d startPos, Vec3d mainRayEnd, List<Vec3d> rays, double distance, Predicate<Entity> predicate, double margin)
    {
        World world = caster.getWorld();
        Box box = caster.getBoundingBox().stretch(mainRayEnd.subtract(startPos)).expand(1.0, 1.0, 1.0);

        List<Entity> boxEntities = world.getOtherEntities(caster, box, predicate);
        List<HitResult> list = new ArrayList<>();
        for (Vec3d ray : rays)
        {
            Vec3d endPos = startPos.add(ray);

            double minDistance = distance;
            @Nullable EntityHitResult minEntity = null;
            for (var entity : boxEntities)
            {
                Vec3d hitPos = entity.getBoundingBox().expand(entity.getTargetingMargin() + margin).raycast(startPos, endPos).orElse(null);
                if (hitPos != null)
                {
                    double dist = hitPos.distanceTo(startPos);
                    if (dist <= minDistance)
                    {
                        minDistance = dist;
                        minEntity = new EntityHitResult(entity, hitPos);
                    }
                }
            }

            if (minEntity != null)
            {
                list.add(minEntity);
            }
            else
            {
                RaycastContext ctx = new RaycastContext(startPos, endPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
                BlockHitResult blockResult = world.raycast(ctx);
                list.add(blockResult);
            }
        }

        return list;
    }

    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, float width, int maxTime, double showRadius)
    {
        Vec3d col = new Vec3d(255, 90, 90);
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BULLET_TRAIL, world, pos, end, col, 0.1f, 1);
        }
    }
}
