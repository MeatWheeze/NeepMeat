package com.neep.neepmeat.fluid;

import com.neep.neepmeat.init.NMFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnrichedBloodFluid extends RealisticFluid
{
    @Override
    public Fluid getStill()
    {
        return NMFluids.STILL_ENRICHED_BLOOD;
    }

    @Override
    public Fluid getFlowing()
    {
        return NMFluids.FLOWING_ENRICHED_BLOOD;
    }

    @Override
    public Item getBucketItem()
    {
        return NMFluids.ENRICHED_BLOOD_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState)
    {
        return NMFluids.ENRICHED_BLOOD.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    @Override
    public void onScheduledTick(World world, BlockPos pos, FluidState state)
    {
        this.tryFlow(world, pos, state);
    }

    public static class Flowing extends EnrichedBloodFluid
    {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
        {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState)
        {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState)
        {
            return false;
        }
    }

    public static class Still extends EnrichedBloodFluid
    {
        @Override
        public int getLevel(FluidState fluidState)
        {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState)
        {
            return true;
        }
    }
}
