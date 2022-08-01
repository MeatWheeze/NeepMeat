package com.neep.neepmeat.fluid_transfer;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.AbstractPipeBlock;
import com.neep.neepmeat.transport.block.fluid_transport.ICapillaryPipe;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PipeState
{
    List<Direction> connections = new ArrayList<>();
    protected float pressure;
    protected int distance;
    protected boolean capillary;
    protected final ISpecialPipe special;

    public PipeState(BlockState state)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            for (Direction direction : Direction.values())
            {
                if (state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
                {
                    connections.add(direction);
                }
            }
        }
        else if (state.getBlock() instanceof BaseFacingBlock facing)
        {
            connections.add(state.get(BaseFacingBlock.FACING));
            connections.add(state.get(BaseFacingBlock.FACING).getOpposite());
        }
        this.capillary = state.getBlock() instanceof ICapillaryPipe;
        this.special = state.getBlock() instanceof ISpecialPipe specialPipe ? specialPipe : null;
    }

    @Override
    public String toString()
    {
//        return Float.toString(pressure);
        return "PipeState{connection=" + connections + ", special:" + special + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) // ?????
        {
            return true;
        }
        if (o instanceof PipeState state)
        {
            return getDistance() == state.getDistance() && connections.equals(state.connections);
        }
        return false;
    }

    public boolean canFluidFlow(Direction bias, BlockState state)
    {
        if (!isSpecial())
        {
            return true;
        }
        return special.canTransferFluid(bias, state);
    }

    public boolean isCapillary()
    {
        return capillary;
    }

    public boolean isSpecial()
    {
        return special != null;
    }

    public ISpecialPipe getSpecial()
    {
        return special;
    }

    public float getPressure()
    {
        return pressure;
    }

    public int getDistance()
    {
        return distance;
    }

    public interface ISpecialPipe
    {
        FilterFunction getFlowFunction(World world, Direction bias, BlockPos pos, BlockState state);

//        default ISpecialPipe andThen(ISpecialPipe next)
//        {
//            return (bias, state, flow) -> next.apply(
//        }
        boolean canTransferFluid(Direction bias, BlockState state);

    }

    public static long zero(FluidVariant variant, long l)
    {
        return 0;
    }

    public static long identity(FluidVariant variant, long l)
    {
        return l;
    }

    @FunctionalInterface
    public interface FilterFunction
    {
        long applyVariant(FluidVariant variant, long l);

        default FilterFunction andThen(FilterFunction after)
        {
            Objects.requireNonNull(after);
            return (v, l) ->
            {
                return after.applyVariant(v, this.applyVariant(v, l));
            };
        }
    }

//    public static class RouteFlowLimit
//    {
//        protected FilterFunction variantFilter;
//        protected Function<Long, Long> flowFunction;
//
//        public RouteFlowLimit(FilterFunction variantFilter, Function<Long, Long> flowFunction)
//        {
//            this.variantFilter = variantFilter;
//            this.flowFunction = flowFunction;
//        }
//    }
}
