package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector4d;

import java.util.Optional;

import static com.neep.meatweapons.item.BaseGunItem.hitScan;

public class LongBoiModule extends ShooterModule
{
    private final Random shotRandom = Random.create();

    public LongBoiModule(MeatgunComponent.Listener listener)
    {
        super(listener, 4, 20);
    }

    public LongBoiModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.LONG_BOI;
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
        double d = 0.1;
        double yaw = yawd + d * 0.1 * (shotRandom.nextFloat() - 0.5);
        double pitch = pitchd + d * 0.1 * (shotRandom.nextFloat() - 0.5);

        Vec3d pos = player.getEyePos();
        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitchd).rotateY((float) -yawd);
        pos = pos.add(transform);

        int range = 100;
        Vec3d end = pos.add(GunItem.getRotationVector(pitch, yaw).multiply(range));
        Optional<Entity> target = hitScan(player, pos, end, range, this::syncBeamEffect);
        if (target.isPresent())
        {
            Entity entity = target.get();
            target.get().damage(BulletDamageSource.create(world, player, 0.1f), 25);
            entity.timeUntilRegen = 0;
        }

        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 10, 1.4f,0.7f, 0.03f);
        world.playSoundFromEntity(null, player, NMSounds.LONG_BOI_FIRE, SoundCategory.PLAYERS, 1f, 1f);
        if (world instanceof ServerWorld serverWorld)
        {
            Vector4d v = new Vector4d(0, 0, -34 / 16f, 1);
            v.mul(this.transform);
            serverWorld.spawnParticles(
                    new MuzzleFlashParticleType.MuzzleFlashParticleEffect(MWParticles.LONG_BOI_MUZZLE_FLASH, player, v.x, v.y, v.z, 1.9f, 4)
                    , pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0.1);
        }
    }

    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, float width, int maxTime, double showRadius)
    {
        Vec3d col = new Vec3d(214, 214, 214);
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.LONG_BULLET_TRAIL, world, pos, end, col, 0.5f, 40);
        }
    }
}
