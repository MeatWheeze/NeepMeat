package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.entity.BounceGrenadeEntity;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.meatweapons.particle.MuzzleFlashParticleType;
import com.neep.neepmeat.init.NMSounds;
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
        super(listener, 4, 4, 15, AmmunitionType.BALLISTIC);
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

        float speed = 0.8f;

        BounceGrenadeEntity entity = new BounceGrenadeEntity(world, 1.7f, 40, false);
        entity.setPos(pos.x, pos.y, pos.z);
        entity.setPosition(pos.x, pos.y, pos.z);
        entity.setVelocity(player, (float) Math.toDegrees(pitch), (float) Math.toDegrees(yaw), 0, speed, 0.1f);
        entity.setOwner(player);
        if (listener.getHolder().containsType(MeatgunModules.HOMING_BRAIN))
        {
            entity.setHomingRadius(4);
        }
        entity.setHomingSpeed(0.05f, false);
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
}
