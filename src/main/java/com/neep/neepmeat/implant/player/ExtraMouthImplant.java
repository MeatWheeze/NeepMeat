package com.neep.neepmeat.implant.player;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.MeatCartonItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
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
        if (player.world.getTime() % 20 != 0) return;

        HungerManager hungerManager = player.getHungerManager();
        if (hungerManager.isNotFull())
        {
            int foodLevel = hungerManager.getFoodLevel();
            ItemStack stack = getFood(MAX_FOOD - foodLevel, hungerManager.getSaturationLevel(), player.getMaxHealth() - player.getHealth());
            if (stack != null)
            {
                eatFood(player, stack);
            }
        }
    }

    @Nullable
    protected ItemStack getFood(int emptyHunger, float saturation, float emptyHearts)
    {
        Inventory inventory = player.getInventory();
        List<ItemStack> foodStacks = Lists.newArrayList();
        for (int i = 0; i < inventory.size(); ++i)
        {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem().isFood() && stack.getItem().getFoodComponent() != null)
            {
                foodStacks.add(stack);
            }
        }

        if (foodStacks.isEmpty())
            return null;


        if (emptyHearts > 3)
        {
            // Prioritise high saturation if damaged
            foodStacks.sort(Comparator.comparingDouble(s -> ((ItemStack) s).getItem().getFoodComponent().getSaturationModifier()).reversed());

            return foodStacks.get(0);
        }
        else
        {
            foodStacks.sort(Comparator.comparingInt(s -> ((ItemStack) s).getItem().getFoodComponent().getHunger()).reversed());

            for (var stack : foodStacks)
            {
                if (emptyHunger >= stack.getItem().getFoodComponent().getHunger())
                {
                    return stack;
                }
            }
        }


        // If no small items are available, wait for empty hunger to drop below half of the smallest item's hunger.
        ItemStack smallest = foodStacks.get(foodStacks.size() - 1);
        if (emptyHunger > smallest.getItem().getFoodComponent().getHunger() / 2)
            return smallest;

        return null;
    }

    protected static void eatFood(PlayerEntity player, ItemStack stack)
    {
        Item item = stack.getItem();

        // Hopefully nobody notices this.
        if (item instanceof MeatCartonItem meatCarton)
        {
            meatCarton.eatFood(player, player.world, stack);
        }
        else
        {
            player.eatFood(player.world, stack);
//            player.getHungerManager().eat(item, stack);
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
