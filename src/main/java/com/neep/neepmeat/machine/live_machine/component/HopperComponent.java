package com.neep.neepmeat.machine.live_machine.component;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.LiveMachines;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.BlockPos;

public class HopperComponent implements LivingMachineComponent
{
    private final HopperBlockEntity be;

    public HopperComponent(HopperBlockEntity be)
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
        return LiveMachines.HOPPER;
    }

    public HopperBlockEntity get()
    {
        return be;
    }
}
