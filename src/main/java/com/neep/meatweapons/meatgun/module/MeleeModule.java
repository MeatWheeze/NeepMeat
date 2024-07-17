package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.network.MeatgunNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class MeleeModule extends AbstractMeatgunModule
{
    public MeleeModule(RootModuleHolder.Listener listener, List<ModuleSlot> slots)
    {
        super(listener, slots);
    }

    protected boolean fireBeam(World world, PlayerEntity player, double pitch, double yaw, double range)
    {
        Vec3d pos = player.getEyePos();

        Vec3d end = pos.add(GunItem.getRotationVector(pitch, yaw).multiply(range + 1));
        @Nullable EntityHitResult target = BaseGunItem.hitScan(player, pos, end, range, e -> e != player.getVehicle(),
                (world1, pos1, end1, width, maxTime, showRadius) -> {}, 0.4f).orElse(null);

        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 7, 0.2f, 0.7f, 0.03f);

        if (target != null && !target.getEntity().hasPassenger(player))
        {
            target.getEntity().damage(world.getDamageSources().playerAttack(player), 7);
//            target.timeUntilRegen = 0; // For bullets
            return true;
        }
        return false;
//        if (world instanceof ServerWorld serverWorld)
//        {
//            Vector4d v = new Vector4d(0, 0, -13 / 16f, 1);
//            v.mul(this.transform);
//            serverWorld.spawnParticles(
//                    new MuzzleFlashParticleType.MuzzleFlashParticleEffect(MWParticles.NORMAL_MUZZLE_FLASH, player, v.x, v.y, v.z, 2.2f, 1)
//                    , pos.getX(), pos.getY(), pos.getZ(),
//                    1, 0, 0, 0, 0.1);
//        }
    }
}
