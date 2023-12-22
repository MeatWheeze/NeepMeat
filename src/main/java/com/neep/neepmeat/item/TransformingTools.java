package com.neep.neepmeat.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

public class TransformingTools
{
    public static final String ROOT_ID = "neepmeat:transformingTool";

    public static boolean isTransformingTool(ItemStack stack)
    {
        return stack.getOrCreateNbt().contains(ROOT_ID);
    }

    public static void swap(ItemStack oldStack, PlayerEntity player)
    {
        if (isTransformingTool(oldStack))
        {
            NbtCompound nbt = oldStack.getOrCreateNbt().getCompound(ROOT_ID);

            // Retrieve alternate stack from NBT
            ItemStack newStack = ItemStack.fromNbt(nbt);
            NbtCompound oldStackNbt = oldStack.writeNbt(new NbtCompound());

            // Convert the old stack to NBT and store it in the new stack
            NbtCompound newStackNbt = newStack.getOrCreateNbt();
            newStackNbt.put(ROOT_ID, oldStackNbt);

            player.setStackInHand(Hand.MAIN_HAND, newStack);
        }
    }
}
