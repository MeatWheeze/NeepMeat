package com.neep.neepmeat.screen_handler.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class PatternSlot extends Slot
{
    protected final int maxAmount = 1;

    public PatternSlot(Inventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    public void onQuickTransfer(ItemStack newItem, ItemStack original)
    {

    }

    public ItemStack takeStackRange(int min, int max, PlayerEntity player)
    {
        return ItemStack.EMPTY;
    }

    public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player)
    {
        return super.tryTakeStackRange(min, max, player);
    }

    @Override
    public void setStackNoCallbacks(ItemStack stack)
    {
        super.setStackNoCallbacks(stack);
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int amount)
    {
        ItemStack thisStack = stack.copy();
        thisStack.setCount(1);
        this.setStackNoCallbacks(thisStack);
        return stack;
    }

    @Override
    public ItemStack takeStack(int amount)
    {
        this.inventory.removeStack(this.getIndex());
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity)
    {
        this.inventory.removeStack(this.getIndex());
        return false;
    }
}
