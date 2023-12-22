package com.neep.neepmeat.machine;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHeatable
{
    void setBurning();

    default int getCookTimeTotal() {return 0;}

    default int getCookTime() {return 0;}

    default void setCookTime(int time) {}

    default void updateState(World world, BlockPos pos, BlockState oldState) {};

    void setHeat(float heat);
    float getHeat();

    static int getFurnaceTickIncrement(float heat)
    {
        return (int) Math.floor(heat * 3) + 1;
    }
}
