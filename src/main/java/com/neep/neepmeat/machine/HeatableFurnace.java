package com.neep.neepmeat.machine;

import com.neep.neepmeat.machine.IHeatable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// An extension of IHeatable that implements default methods for use with interface injection.
// Only to be used for MixinAbstractFurnaceBlockEntity.
public interface HeatableFurnace extends IHeatable
{
    @Override
    default void setBurning()
    {

    }

    @Override
    default void updateState(World world, BlockPos pos, BlockState oldState)
    {

    }

    @Override
    default int getCurrentBurnTime()
    {
        return 0;
    }

    @Override
    default int getFuelTime() { return 0; }

    @Override
    default int getCookTimeTotal() { return 0; }

    @Override
    default int getCookTime() { return 0; }

    @Override
    default void setCookTime(int time) {}

    @Override
    default void setHeatMultiplier(float multiplier) {}

    @Override
    default float getHeatMultiplier() { return 0; }

    default boolean isCooking() { return false; }
}
