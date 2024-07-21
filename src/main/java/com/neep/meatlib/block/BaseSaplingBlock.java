package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class BaseSaplingBlock extends SaplingBlock implements MeatlibBlock
{
    protected BlockItem blockItem;

    public BaseSaplingBlock(RegistrationContext ctx, SaplingGenerator generator, ItemSettings itemSettings, Settings settings)
    {
        super(generator, settings);
        this.blockItem = itemSettings.create(this, ctx, itemSettings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        super.randomTick(state, world, pos, random);
    }

    @Override
    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random)
    {
        super.generate(world, pos, state, random);
    }
}
