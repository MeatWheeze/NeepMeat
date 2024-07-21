package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseLeavesBlock;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.init.NMParticles;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BloodBubbleLeavesBlock extends BaseLeavesBlock
{
    public BloodBubbleLeavesBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, settings
            .strength(0.2f)
            .ticksRandomly()
            .nonOpaque()
            .allowsSpawning((p1, p2, p3, p4) -> false)
            .suffocates((p1, p2, p3) -> false).blockVision(((state, world, pos) -> false)));
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

//    @Override
//    public boolean autoGenDrop()
//    {
//        return false;
//    }

    @Override
    public ItemConvertible dropsLike()
    {
        return null;
    }

    public static class FruitingBloodBubbleLeavesBlock extends BloodBubbleLeavesBlock
    {
        public FruitingBloodBubbleLeavesBlock(RegistrationContext ctx, Settings settings)
        {
            super(ctx, settings);
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
