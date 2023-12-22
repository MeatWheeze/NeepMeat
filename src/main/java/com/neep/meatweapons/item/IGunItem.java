package com.neep.meatweapons.item;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public interface IGunItem
{
    int ANIM_FIRE = 0;
    int ANIM_RELOAD = 1;

    Vec3d getMuzzleOffset(PlayerEntity player, ItemStack stack);

    default void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType) {}

    default void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType) {}
    default void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType) {}

    void playSound(World world, PlayerEntity player, GunSounds sound);

   default void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, double showRadius) {}

   void syncAnimation(World world, PlayerEntity player, ItemStack stack, int animation, boolean broadcast);

    Random getRandom();

    int getShots(ItemStack stack, int trigger);

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
