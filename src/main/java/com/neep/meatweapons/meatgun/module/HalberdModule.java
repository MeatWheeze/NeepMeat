package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.meatweapons.entity.HitOnCollideEntity;
import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.meatweapons.particle.MuzzleFlashParticleType;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector4d;

import java.util.Optional;

public class HalberdModule extends AbstractMeatgunModule
{
    private boolean triggerHeld;
    private int triggerTicks;

    public HalberdModule(MeatgunComponent.Listener listener)
    {
        super(listener);
    }

    public HalberdModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.HALBERD;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.trigger(world, player, stack, id, pitch, yaw, handType);

        if (id == 2)
        {
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("blade_swing_down"); // Sword attack
        }

        if (id == 1)
        {
            if (player.isSprinting())
            {
                triggerHeld = true;
                listener.markDirty();
                MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("upper_thrust");
            }
        }
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.release(world, player, stack, id, pitch, yaw, handType);

        if (id == 1)
        {
            if (triggerTicks >= 10)
            {
                thrustForwards(world, player, pitch, yaw);
            }

            triggerHeld = false;
            triggerTicks = 0;
            listener.markDirty();
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.tickTrigger(world, player, stack, id, pitch, yaw, handType);

        if (id == 1)
        {
            if (player.isSprinting())
            {
                triggerTicks = Math.min(triggerTicks + 1, 30);

                if (!triggerHeld)
                {
                    triggerHeld = true;
                    MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("upper_thrust");
                    listener.markDirty();
                }
            }
        }
    }

    @Override
    public void tick(PlayerEntity player)
    {
        super.tick(player);

        if (!player.isSprinting())
        {
            triggerTicks = 0;
            if (triggerHeld)
            {
                triggerHeld = false;
                listener.markDirty();
            }
        }
    }

    protected void thrustForwards(World world, PlayerEntity player, double pitch, double yaw)
    {
        double f = 0.7;
//        player.addVelocity(f * Math.sin(pitch), 1.05, f * Math.cos(pitch));
        player.addVelocity(f * Math.sin(-yaw), 0.4, f * Math.cos(-yaw));
        player.velocityModified = true;
//        player.setVelocity(f * Math.sin(pitch), 1.05, f * Math.cos(pitch));
        player.velocityDirty = true;

        ((HitOnCollideEntity) player).meatweapons$setActiveTicks(20);
        ((HitOnCollideEntity) player).meatweapons$setDamage(6);

//        fireBeam(world, player, pitch, yaw);
    }

    protected void fireBeam(World world, PlayerEntity player, double pitch, double yaw)
    {
        Vec3d pos = player.getEyePos();

        Vec3d end = pos.add(GunItem.getRotationVector(pitch, yaw).multiply(3));
//        System.out.println();
        Optional<Entity> target = BaseGunItem.hitScan(player, pos, end, 40, (world1, pos1, end1, width, maxTime, showRadius) -> {});
        if (target.isPresent())
        {
            Entity entity = target.get();
            target.get().damage(BulletDamageSource.create(world, player, 0.1f), 7);
            entity.timeUntilRegen = 0;
        }

        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 7, 0.2f,0.7f, 0.03f);
        world.playSoundFromEntity(null, player, NMSounds.CHUGGER_FIRE, SoundCategory.PLAYERS, 1f, 1f);
        if (world instanceof ServerWorld serverWorld)
        {
            Vector4d v = new Vector4d(0, 0, -13 / 16f, 1);
            v.mul(this.transform);
            serverWorld.spawnParticles(
                    new MuzzleFlashParticleType.MuzzleFlashParticleEffect(MWParticles.NORMAL_MUZZLE_FLASH, player, v.x, v.y, v.z, 2.2f, 1)
                    , pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0.1);
        }
    }

    // I can't think of a better place to put this. It needs to be outside the mixin so that it can be hot-swapped.
    public static void onEntityCollide(PlayerEntity origin, Entity target, int ticksRemaining, float damage)
    {
        if (!origin.getWorld().isClient())
        {
            Vec3d velocity = origin.getVelocity();
            if (ticksRemaining > 0 && Math.abs(velocity.x) > 0.05 && Math.abs(velocity.z) > 0.05)
            {
                target.damage(origin.getWorld().getDamageSources().playerAttack(origin), damage);
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("trigger_held", triggerHeld);
        nbt.putInt("trigger_ticks", triggerTicks);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.triggerHeld = nbt.getBoolean("trigger_held");
        this.triggerTicks = nbt.getInt("trigger_ticks");
    }

    public boolean triggerHeld()
    {
        return triggerHeld;
    }
}
