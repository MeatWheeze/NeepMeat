package com.neep.neepmeat.machine;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Only to be used for MixinAbstractFurnaceBlockEntity.
public interface HeatableFurnace
{
    static Heatable of(AbstractFurnaceBlockEntity furnace)
    {
        return new Wrapper(furnace);
    }

    default void neepMeat$setBurning()
    {

    }

    default void neepMeat$updateState(World world, BlockPos pos, BlockState oldState)
    {

    }

    default int neepMeat$getCookTimeTotal() { return 0; }

    default int neepMeat$getCookTime() { return 0; }

    default void neepMeat$setCookTime(int time) {}

    default void neepMeat$setHeat(float heat) {}

    default float neepMeat$getHeat() { return 0; }

    default boolean neepMeat$isCooking() { return false; }

    class Wrapper implements Heatable
    {
        private final HeatableFurnace furnace;

        protected Wrapper(HeatableFurnace furnace)
        {
            this.furnace = furnace;
        }

        @Override
        public void setBurning()
        {
            furnace.neepMeat$setBurning();
        }

        @Override
        public void updateState(World world, BlockPos pos, BlockState oldState)
        {
            furnace.neepMeat$updateState(world, pos, oldState);
        }

        @Override
        public void setHeat(float heat)
        {
            furnace.neepMeat$setHeat(heat);
        }
    }
}
