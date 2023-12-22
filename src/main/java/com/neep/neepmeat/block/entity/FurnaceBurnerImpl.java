package com.neep.neepmeat.block.entity;

import com.neep.neepmeat.api.Burner;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FurnaceBurnerImpl implements Burner
{
    protected final FurnaceAccessor furnace;
    private long outputPower;

    public FurnaceBurnerImpl(FurnaceAccessor furnace)
    {
        this.furnace = furnace;
    }

    @Override
    public void tickPowerConsumption()
    {
        Burner.super.tickPowerConsumption();
        if (furnace.getBurnTime() == 0)
        {
            ItemStack itemStack = furnace.getInventory().get(1);
            int time = furnace.callGetFuelTime(itemStack);
            furnace.setFuelTime(time);
            furnace.setBurnTime(time);
            itemStack.decrement(1);
            updateBlockstate();
        }
        else
        {
            updateBlockstate();
        }
        furnace.setCookTime(0);
        outputPower = furnace.getBurnTime() > 0 ? 40 : 0;
    }

    @Override
    public double getOutputPower()
    {
        return outputPower;
    }

    protected void updateBlockstate()
    {
        AbstractFurnaceBlockEntity furnaceBE = (AbstractFurnaceBlockEntity) (furnace);
        World world = furnaceBE.getWorld();
        world.setBlockState(furnaceBE.getPos(), furnaceBE.getCachedState().with(AbstractFurnaceBlock.LIT, furnace.getBurnTime() > 0));
    }

    public static Burner get(FurnaceBlockEntity be, Void ctx)
    {
        return new FurnaceBurnerImpl((FurnaceAccessor) be);
    }
}
