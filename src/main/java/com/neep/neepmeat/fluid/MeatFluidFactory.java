package com.neep.neepmeat.fluid;

import com.neep.neepmeat.api.processing.MeatFluidHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;

public class MeatFluidFactory extends FluidFactory
{
    public MeatFluidFactory(String namespace, String baseName, boolean isInfinite, int tickRate, int levelDecrease)
    {
        super(namespace, baseName, isInfinite, tickRate, levelDecrease);
    }

    public FlowableFluid registerStill()
    {
        still = Registry.register(Registry.FLUID, new Identifier(namespace, stillName), new Still());
        return still;
    }

    public FlowableFluid registerFlowing()
    {
        flowing = Registry.register(Registry.FLUID, new Identifier(namespace, flowingName), new Flowing());
        return flowing;
    }

    public abstract class MixableMain extends FluidFactory.Main implements MixableFluid
    {
        @Override
        public FluidVariant mixNbt(FluidVariant thisVariant, long thisAmount, FluidVariant otherVariant, long otherAmount)
        {
            NbtCompound nbt = new NbtCompound();

            // Treat hunger and saturation as intrinsic properties.
            float hunger1 = MeatFluidHelper.getHunger(thisVariant);
            float hunger2 = MeatFluidHelper.getHunger(otherVariant);
            MeatFluidHelper.setHunger(nbt, ((hunger1 * thisAmount + hunger2 * otherAmount) / (float) (thisAmount + otherAmount)));

            float sat1 = MeatFluidHelper.getSaturation(thisVariant);
            float sat2 = MeatFluidHelper.getSaturation(otherVariant);
            MeatFluidHelper.setSaturation(nbt, ((sat1 * thisAmount + sat2 * otherAmount) / (float) (thisAmount + otherAmount)));

            return FluidVariant.of(this, nbt);
        }

        @Override
        protected int getLevelDecreasePerBlock(WorldView worldView)
        {
            return levelDecrease;
        }

        @Override
        public int getTickRate(WorldView worldView)
        {
            return tickRate;
        }

        @Override
        public Fluid getStill()
        {
            return still;
        }

        @Override
        public Fluid getFlowing()
        {
            return flowing;
        }

        @Override
        public Item getBucketItem()
        {
            return bucketItem;
        }

        @Override
        protected BlockState toBlockState(FluidState fluidState)
        {
            return block.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
        }
    }

    public class Flowing extends MixableMain
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

    public class Still extends MixableMain
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
