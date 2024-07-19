package com.neep.meatweapons.item;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public interface GunItem extends BeamEffectProvider, TriggerReceiver
{
    static GunItem getGun(ItemStack stack)
    {
        return stack.getItem() instanceof GunItem gun ? gun : null;
    }

    Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack);

    default void triggerClient(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType) {}
    default void releaseClient(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType) {}

    void playSound(World world, LivingEntity entity, GunSounds sound);

   default void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, float width, int maxTime, double showRadius) {}

   void syncAnimation(World world, LivingEntity player, ItemStack stack, String animation, boolean broadcast);

    Random getRandom();

    int getShots(ItemStack stack, int trigger);

    static Vec3d getRotationVector(double pitchRad, double yawRad)
    {
        float f = (float) pitchRad;
        float g = (float) -yawRad;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    // Removes ammunition from inventory. Returns null if none present.
    static ItemStack removeStack(Item type, PlayerEntity player)
    {
        for (int i = 0; i < player.getInventory().size(); ++i)
        {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem().equals(type))
            {
                return stack;
            }
        }
        return null;
    }

    default boolean redirectClicks() { return true; };

    enum GunSounds
    {
        FIRE_PRIMARY,
        FIRE_SECONDARY,
        RELOAD,
        EMPTY,
    }
}
