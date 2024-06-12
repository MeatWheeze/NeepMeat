package com.neep.neepmeat.fluid;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.item.BaseBucketItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.world.WorldView;

public class BuiltFluid
{
    private final int levelDecrease;
    private final int tickRate;
    private final Item bucketItem;
    private final Flowing flowing;
    private final Still still;

    private final FluidBlock block;

    public BuiltFluid(String namespace, String baseName, int levelDecrease, int tickRate, boolean makeBlock, boolean makeItem)
    {
        this.levelDecrease = levelDecrease;
        this.tickRate = tickRate;

        still = Registry.register(Registries.FLUID, new Identifier(namespace, baseName), new Still());
        flowing = Registry.register(Registries.FLUID, new Identifier(namespace, "flowing_" + baseName), new Flowing());

        if (makeBlock)
            block = Registry.register(Registries.BLOCK, new Identifier(namespace, baseName), new FluidBlock(still, MeatlibBlockSettings.copy(Blocks.WATER)){});
        else
            block = null;

        if (makeItem)
            bucketItem = new BaseBucketItem(namespace, baseName + "_bucket", still, new MeatlibItemSettings().maxCount(1).recipeRemainder(Items.BUCKET).group(NMItemGroups.GENERAL));
        else
            bucketItem = null;
    }

    public Still still()
    {
        return still;
    }

    public Flowing flowing()
    {
        return flowing;
    }

    public FluidBlock getBlock()
    {
        return block;
    }

    public FluidVariant variant()
    {
        return FluidVariant.of(still);
    }

    public Item getItem()
    {
        return bucketItem;
    }

    protected abstract class Main extends BaseFluid
    {
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

    private class Flowing extends Main
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

    private class Still extends Main
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
