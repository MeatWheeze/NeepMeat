package com.neep.neepmeat.machine.live_machine.component;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.LiveMachines;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.BlockPos;

public class ItemOutputComponent implements LivingMachineComponent
{
    private final ChestBlockEntity be;

    public ItemOutputComponent(ChestBlockEntity be)
    {

        this.be = be;
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return be.isRemoved();
    }

    @Override
    public ComponentType<?> getComponentType()
    {
        return LiveMachines.ITEM_OUTPUT;
    }

    public ChestBlockEntity get()
    {
        return be;
    }
}
