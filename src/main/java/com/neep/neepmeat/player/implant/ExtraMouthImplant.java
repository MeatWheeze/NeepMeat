package com.neep.neepmeat.player.implant;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.MeatCartonItem;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ExtraMouthImplant implements EntityImplant
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "extra_mouth");
    protected static final int MAX_FOOD = 20;
    protected final PlayerEntity player;

    public ExtraMouthImplant(PlayerEntity player)
    {
        this.player = player;
    }

    @Override
    public void tick()
    {
        if (player.world.getTime() % 20 != 0) return;

        HungerManager hungerManager = player.getHungerManager();
        if (hungerManager.isNotFull())
        {
            int foodLevel = hungerManager.getFoodLevel();
            ItemStack stack = getFood(MAX_FOOD - foodLevel);
            if (stack != null)
            {
                eatFood(player, stack);
            }
        }
    }

    @Nullable
    protected ItemStack getFood(int maxHunger)
    {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); ++i)
        {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem().isFood())
            {
                int stackHunger = stack.getItem().getFoodComponent().getHunger();
                if (stackHunger <= maxHunger) return stack;
            }
        }
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
