package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseLeavesBlock;
import com.neep.neepmeat.init.NMParticles;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BloodBubbleLeavesBlock extends BaseLeavesBlock
{
    public BloodBubbleLeavesBlock(String name, Settings settings)
    {
        super(name, settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
        if (random.nextInt(12) != 1)
            return;

        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOpaque() && blockState.isSideSolidFullSquare(world, blockPos, Direction.UP))
            return;

        double d = (double)pos.getX() + random.nextDouble();
        double e = (double)pos.getY() - 0.05;
        double f = (double)pos.getZ() + random.nextDouble();
        world.addParticle(NMParticles.BLOOD_DRIP, d, e, f, 0.0, 0.0, 0.0);
    }

    public static class FruitingBloodBubbleLeavesBlock extends BloodBubbleLeavesBlock
    {
        public FruitingBloodBubbleLeavesBlock(String name, Settings settings)
        {
            super(name, settings);
        }

        @Override
        public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
        {
            if (random.nextInt(5) != 1)
                return;

            BlockPos blockPos = pos.down();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOpaque() && blockState.isSideSolidFullSquare(world, blockPos, Direction.UP))
                return;

            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() - 0.05;
            double f = (double)pos.getZ() + random.nextDouble();
            world.addParticle(NMParticles.BLOOD_DRIP, d, e, f, 0.0, 0.0, 0.0);
        }
    }
}
