package com.neep.neepmeat.machine.small_compressor;

import com.neep.meatlib.util.NbtSerialisable;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;

public class BurnerBehaviour implements NbtSerialisable
{
    private final Inventory inventory;
    private final BooleanConsumer updateBurn;
    private int burnTime;
    private int maxBurnTime;

    private final Delegate delegate = new Delegate();

    public BurnerBehaviour(Inventory inventory, BooleanConsumer updateBurn)
    {
        this.inventory = inventory;
        this.updateBurn = updateBurn;
    }

                            @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("max_burn_time", maxBurnTime);
        nbt.putInt("burn_time", burnTime);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.maxBurnTime = nbt.getInt("max_burn_time");
        this.burnTime = nbt.getInt("burn_time");
    }

    public int getBurnTime()
    {
        return burnTime;
    }

    public int getMaxBurnTime()
    {
        return maxBurnTime;
    }

    public void tick()
    {
        burnTime = Math.max(0, burnTime - 1);

        if (burnTime == 0)
        {
            Integer time = FuelRegistry.INSTANCE.get(inventory.getStack(0).getItem());
            if (time != null)
            {
                maxBurnTime = time;
                burnTime = maxBurnTime;
                inventory.getStack(0).decrement(1);
            }
            updateBurn.accept(burnTime > 0);
        }
    }

    public PropertyDelegate getDelegate()
    {
        return delegate;
    }

    private class Delegate implements PropertyDelegate
    {
        @Override
        public int get(int index)
        {
            return switch (index)
            {
                case 0 -> burnTime;
                case 1 -> maxBurnTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {

        }

        @Override
        public int size()
        {
            return 2;
        }
    }
}
