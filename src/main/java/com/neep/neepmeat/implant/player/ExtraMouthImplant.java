package com.neep.neepmeat.implant.player;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.MeatCartonItem;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class ExtraMouthImplant implements EntityImplant
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "extra_mouth");
    protected static final int MAX_FOOD = 20;
    protected final PlayerEntity player;

    public ExtraMouthImplant(Entity entity)
    {
        this.player = (PlayerEntity) entity;
    }

    @Override
    public void tick()
    {
        if (player.getWorld().getTime() % 20 != 0) return;

        HungerManager hungerManager = player.getHungerManager();
        if (hungerManager.isNotFull())
        {
            int foodLevel = hungerManager.getFoodLevel();
            IntObjectPair<ItemStack> stack = getFood(MAX_FOOD - foodLevel, hungerManager.getSaturationLevel(), player.getMaxHealth() - player.getHealth());
            if (stack != null)
            {
                eatFood(player, stack.value(), stack.keyInt());
            }
        }
    }

    @Nullable
    protected IntObjectPair<ItemStack> getFood(int emptyHunger, float saturation, float emptyHearts)
    {
        Inventory inventory = player.getInventory();
        List<IntObjectPair<ItemStack>> foodStacks = Lists.newArrayList();
        for (int i = 0; i < inventory.size(); ++i)
        {
            ItemStack stack = inventory.getStack(i);
            if (
                    stack.getRarity() == Rarity.COMMON  // Avoid eating golden apples and expenisve things
                    && stack.getItem().isFood()
                    && stack.getItem().getFoodComponent() != null)
            {
                foodStacks.add(IntObjectPair.of(i, stack));
            }
        }

        if (foodStacks.isEmpty())
            return null;

        if (emptyHearts > 3)
        {
            // Prioritise high saturation if damaged
            foodStacks.sort(Comparator.<IntObjectPair<ItemStack>>comparingDouble(s -> s.value().getItem().getFoodComponent().getSaturationModifier()).reversed());

            return foodStacks.get(0);
        }
        else
        {
            foodStacks.sort(Comparator.<IntObjectPair<ItemStack>>comparingInt(s -> s.value().getItem().getFoodComponent().getHunger()).reversed());

            for (var pair : foodStacks)
            {
                if (emptyHunger >= pair.value().getItem().getFoodComponent().getHunger())
                {
                    return pair;
                }
            }
        }

        // If no small items are available, wait for empty hunger to drop below half of the smallest item's hunger.
        IntObjectPair<ItemStack> smallest = foodStacks.get(foodStacks.size() - 1);
        if (emptyHunger > smallest.value().getItem().getFoodComponent().getHunger() / 2)
            return smallest;

        return null;
    }

    protected static void eatFood(PlayerEntity player, ItemStack stack, int slot)
    {
        Item item = stack.getItem();

        // Hopefully nobody notices this.
        if (item instanceof MeatCartonItem meatCarton)
        {
            meatCarton.eatFood(player, player.getWorld(), stack);
        }
        else
        {
            ItemStack remainder = stack.finishUsing(player.getWorld(), player);
            if (remainder != stack)
            {
                player.getInventory().setStack(slot, remainder);
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }
}
