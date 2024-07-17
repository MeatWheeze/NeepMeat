package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.entity.BounceGrenadeEntity;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.meatweapons.particle.MuzzleFlashParticleType;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector4d;

public class GrenadeLauncherModule extends ShooterModule
{
    private final Random shotRandom = Random.create();

    public GrenadeLauncherModule(RootModuleHolder.Listener listener)
    {
        super(listener, 8, 15);
        shotsRemaining = maxShots;
    }

    public GrenadeLauncherModule(RootModuleHolder.Listener listener, NbtCompound nbt)
    {
        this(listener);
        readNbt(nbt);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.GRENADE_LAUNCHER;
    }

    @Override
    public void tick(PlayerEntity player)
    {
        cooldown = Math.max(0, cooldown - 1);
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
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
                0,
                .25);
    }

    protected void fireBeam(World world, PlayerEntity player, ItemStack stack, double pitchd, double yawd)
    {
        double d = 0.5;
        double yaw = yawd + d * 0.1 * (shotRandom.nextFloat() - 0.5);
        double pitch = pitchd + d * 0.1 * (shotRandom.nextFloat() - 0.5);

        Vec3d pos = player.getEyePos();
        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitchd).rotateY((float) -yawd);
        pos = pos.add(transform);

        double speed = 0.8;
        Vec3d vel = GunItem.getRotationVector(pitch, yaw).multiply(speed);

        BounceGrenadeEntity entity = new BounceGrenadeEntity(world, 1.7f, 40, false, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
        world.spawnEntity(entity);

        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 7, 0.2f,0.7f, 0.03f);
        world.playSoundFromEntity(null, player, NMSounds.GRENADE_LAUNCHER_FIRE, SoundCategory.PLAYERS, 1f, 1f);
        if (world instanceof ServerWorld serverWorld)
        {
            Vector4d v = new Vector4d(0, 0, -13 / 16f, 1);
            v.mul(this.transform);
            serverWorld.spawnParticles(
                    new MuzzleFlashParticleType.MuzzleFlashParticleEffect(MWParticles.BLOB_MUZZLE_FLASH, player, v.x, v.y, v.z, 2.2f, 1)
                    , pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0.1);
        }
    }

    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, float width, int maxTime, double showRadius)
    {
        Vec3d col = new Vec3d(214, 175, 32);
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BULLET_TRAIL, world, pos, end, col, 0.1f, 1);
        }
    }
}
