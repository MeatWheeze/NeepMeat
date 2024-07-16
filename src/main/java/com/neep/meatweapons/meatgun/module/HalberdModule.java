package com.neep.meatweapons.meatgun.module;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.entity.HitOnCollideEntity;
import com.neep.meatweapons.interfaces.HookableEntity;
import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class HalberdModule extends MeleeModule
{
    private boolean triggerHeld;
    private int triggerTicks;

    private int swingDownCooldown;
    private int hookGrabCooldown;

    public HalberdModule(MeatgunComponent.Listener listener)
    {
        super(listener, List.of());
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

        if (id == 2 && swingDownCooldown == 0)
        {
            fireBeam(world, player, pitch, yaw, 3);
            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1, 1);
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("blade_swing_down", null); // Sword attack
            swingDownCooldown = 15;
        }

        if (id == 1)
        {
            if (player.getVehicle() != null)
            {
                hookWhenMounted(world, player, player.getVehicle(), pitch, yaw);

            }
            else if (player.isSprinting())
            {
                triggerHeld = true;
                listener.markDirty();
                MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("upper_thrust", null);
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
                    MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("upper_thrust", null);
                    listener.markDirty();
                }
            }
        }
    }

    @Override
    public void tick(PlayerEntity player)
    {
        super.tick(player);

        swingDownCooldown = Math.max(0, swingDownCooldown - 1);
        hookGrabCooldown = Math.max(0, hookGrabCooldown - 1);

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

    protected void hookWhenMounted(World world, PlayerEntity player, Entity vehicle, double pitch, double yaw)
    {
        if (hookGrabCooldown > 0)
            return;

        hookGrabCooldown = 5;
        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.5f, 1);
        Vec3d pos = player.getEyePos();
        Vec3d end = pos.add(GunItem.getRotationVector(pitch, yaw).multiply(4));
        Entity target = BaseGunItem.hitScan(player, pos, end, 4, e -> e != vehicle, (world1, pos1, end1, width, maxTime, showRadius) -> {}, 0.1f).orElse(null);

        if (target instanceof LivingEntity livingEntity)
        {
            if (player instanceof ServerPlayerEntity serverPlayer)
            {
                hookGrabCooldown = 20;
                world.playSoundFromEntity(null, player, SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, SoundCategory.PLAYERS, 1, 1);

                Vec3d offset = livingEntity.getPos().subtract(player.getPos());

                PacketByteBuf buf = PacketByteBufs.create();
                PacketBufUtil.writeVec3d(buf, offset);
                MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("hook_grab", buf);

                if (target.hasVehicle() && player.getRandom().nextBoolean())
                {
                    target.dismountVehicle();
                }

//                target.addVelocity(
//                        holder.meatweapons$movementX() * 2,
//                        holder.meatweapons$movementY() + 0.3,
//                        holder.meatweapons$movementZ() * 2
//                );
//
//                target.velocityModified = true;
//                target.velocityDirty = true;

                ((HookableEntity) livingEntity).meatweapons$setHookParent(player, offset);
                ((HookableEntity) livingEntity).meatweapons$setHookTicks(20);
                livingEntity.damage(world.getDamageSources().playerAttack(player), 2);
            }
        }
    }

    protected void thrustForwards(World world, PlayerEntity player, double pitch, double yaw)
    {
        double f = 0.7;
//        player.addVelocity(f * Math.sin(pitch), 1.05, f * Math.cos(pitch));
        if (!player.isFallFlying())
        {
            player.addVelocity(f * Math.sin(-yaw), 0.4, f * Math.cos(-yaw));
            player.velocityModified = true;
            player.velocityDirty = true;
        }

        ((HitOnCollideEntity) player).meatweapons$setActiveTicks(20);
        ((HitOnCollideEntity) player).meatweapons$setDamage(6);

        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 7, 0.2f,0.7f, 0.03f);
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

    public static void onOnVehicleMove(VehicleMoveC2SPacket packet, Entity entity, ServerWorld serverWorld, double d, double e, double f, double g, double h, double i, float j, float k, double l, double m, double n, double o, double p, boolean bl, boolean bl2)
    {
        NeepMeat.LOGGER.info("l: {}, m: {}, n: {}", l, m, n);
    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("trigger_held", triggerHeld);
        nbt.putInt("trigger_ticks", triggerTicks);
        nbt.putInt("down_cooldown", swingDownCooldown);
        nbt.putInt("grab_cooldown", hookGrabCooldown);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.triggerHeld = nbt.getBoolean("trigger_held");
        this.triggerTicks = nbt.getInt("trigger_ticks");
        this.swingDownCooldown = nbt.getInt("down_cooldown");
        this.hookGrabCooldown = nbt.getInt("grab_cooldown");
    }

    public boolean triggerHeld()
    {
        return triggerHeld;
    }
}
