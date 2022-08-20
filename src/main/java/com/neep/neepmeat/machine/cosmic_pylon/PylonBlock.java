package com.neep.neepmeat.machine.cosmic_pylon;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.recipe.FluidIngredient;
import com.neep.neepmeat.api.block.pipe.IDataCable;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PylonBlock extends BaseBlock implements BlockEntityProvider, IDataCable
{
    public PylonBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.getBlockEntity(pos) instanceof PylonBlockEntity be && !world.isClient())
        {
            be.update(world.isReceivingRedstonePower(pos));
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        super.scheduledTick(state, world, pos, random);
        if (world.getBlockEntity(pos) instanceof PylonBlockEntity be)
        {
            be.recipeBehaviour.finishRecipe();
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
        super.randomDisplayTick(state, world, pos, random);
        if (world.getBlockEntity(pos) instanceof PylonBlockEntity be && be.hasRecipe)
        {
//            be.recipeBehaviour.load(world);
//            if (be.recipeBehaviour.getRecipeId() != null)
            spawnParticles(world, pos.down(2), 5, 0.4, 0.1);
        }
    }

    public static void spawnParticles(World world, BlockPos pos, int count, double dy, double speed)
    {
        Random random = new Random(pos.asLong());
        for (int i = 0; i <= count; ++i)
        {
            world.addParticle(new SwirlingParticleEffect(NMParticles.BLOCK_SWIRL,
                    Blocks.AMETHYST_BLOCK.getDefaultState(), 0.4, speed), pos.getX() + 0.5, pos.getY() + 0.1 + random.nextFloat(), pos.getZ() + 0.5, 0, dy, 0.1);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.COSMIC_PYLON.instantiate(pos, state);
    }
}
