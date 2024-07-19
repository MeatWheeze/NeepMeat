package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.ShockStaffProjectileEntity;
import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShockStaffModule extends MeleeModule implements AmmunitionRequiringModule, AmmunitionStoringModule
{
    private int swingDownCooldown;
    private int ammoAmount;

    public ShockStaffModule(RootModuleHolder.Listener listener)
    {
        super(listener, List.of());
    }

    public ShockStaffModule(RootModuleHolder.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.SHOCK_STAFF;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        if (id == 2 && swingDownCooldown == 0 && consume(2, player.getInventory(), player))
        {
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("swing_across", null);
            world.playSoundFromEntity(null, player, NMSounds.SHOCK_STAFF_ATTACK, SoundCategory.PLAYERS, 1, 1);
            MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 7, 0.3f,0.7f, 0.02f);
            fireBeam(world, player, pitch, yaw, 3);
            swingDownCooldown = 15;
        }
        if (id == 1 && swingDownCooldown == 0 && consume(1, player.getInventory(), player))
        {
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("swing_across", null);
            world.playSoundFromEntity(null, player, NMSounds.SHOCK_STAFF_ATTACK, SoundCategory.PLAYERS, 1, 1);

            ShockStaffProjectileEntity entity = MeatWeapons.SHOCK_STAFF_PROJECTILE.create(world);
            Vec3d entityPos = player.getEyePos();
            entity.setOwner(player);
            entity.setDamage(1.5);
            entity.setPos(entityPos.x, entityPos.y, entityPos.z);
            entity.setPosition(entityPos.x, entityPos.y, entityPos.z);
            if (listener.getHolder().containsType(MeatgunModules.HOMING_BRAIN))
            {
                entity.setHomingRadius(3);
            }
            entity.setVelocity(player, (float) Math.toDegrees(pitch), (float) Math.toDegrees(yaw), 0, 0.9f, 0);
            entity.setHomingSpeed(0.45f, true);
            world.spawnEntity(entity);
            swingDownCooldown = 15;
        }
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.tickTrigger(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public void tick(PlayerEntity player)
    {
        swingDownCooldown = Math.max(0, swingDownCooldown - 1);
//        projectileCooldown = Math.max(0, projectileCooldown - 1);
    }

    @Override
    protected boolean fireBeam(World world, PlayerEntity player, double pitch, double yaw, double range)
    {
        Vec3d pos = player.getEyePos();

        Vec3d end = pos.add(GunItem.getRotationVector(pitch, yaw).multiply(range + 1));
        @Nullable EntityHitResult target = BaseGunItem.hitScan(player, pos, end, range, e -> e != player.getVehicle(),
                (world1, pos1, end1, width, maxTime, showRadius) -> {}, 0.4f).orElse(null);

        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 4, 0.2f, 0.7f, 0.03f);

        if (target != null && !target.getEntity().hasPassenger(player))
        {
            target.getEntity().damage(world.getDamageSources().playerAttack(player), 7);

            Vec3d targetPos = target.getPos();
            world.playSoundFromEntity(null, player, NMSounds.SHOCK_STAFF_HIT, SoundCategory.PLAYERS, 1, 1);
            ((ServerWorld) world).spawnParticles(MWParticles.SHOCK_STAFF, targetPos.x, targetPos.y, targetPos.z, 10, 0.05, 0.05, 0.05, 0.03);
            return true;
        }
        return false;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("ammo_amount", ammoAmount);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.ammoAmount = nbt.getInt("ammo_amount");
    }

    @Override
    public int capacity()
    {
        return 16;
    }

    @Override
    public AmmunitionType ammoType()
    {
        return AmmunitionType.ENERGY;
    }

    @Override
    public int amount()
    {
        return ammoAmount;
    }

    @Override
    public int insert(int maxAmount)
    {
        int inserted = Math.min(maxAmount, capacity() - ammoAmount);
        if (inserted > 0)
        {
            ammoAmount += inserted;
        }
        return inserted;
    }

    @Override
    public int extract(int maxAmount)
    {
        int extracted = Math.min(ammoAmount, maxAmount);
        if (extracted > 0)
        {
            ammoAmount -= extracted;
            return extracted;
        }
        return 0;
    }

    @Override
    public boolean consume(int amount, Inventory inventory, PlayerEntity player)
    {
        swingDownCooldown = 10;
        if (listener.getHolder().getAmmoOrReload(this, amount, inventory, player))
        {
            listener.markDirty(RootModuleHolder.Reason.SAVE_DATA);
            return true;
        }
        return false;
    }
}
