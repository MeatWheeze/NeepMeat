package com.neep.neepmeat.api.multiblock2;

import com.neep.neepmeat.api.big_block.BigBlockPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class Multiblock2ControllerBlock<T extends MultiBlockStructure<?>> extends Block
{
    private final T structureBlock;

    public Multiblock2ControllerBlock(Settings settings)
    {
        super(settings);
        this.structureBlock = registerStructureBlock();
    }

    protected abstract BigBlockPattern getAssembledPattern(BlockState blockState);
    protected abstract MultiblockUnassembledPattern getUnassembledPattern(BlockState blockState);

    protected abstract T registerStructureBlock();

    public T getStructure()
    {
        return structureBlock;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (assemble(world, pos, state))
            return ActionResult.SUCCESS;

        return super.onUse(state, world, pos, player, hand, hit);
    }

    public boolean assemble(World world, BlockPos origin, BlockState state)
    {
        MultiblockUnassembledPattern assemblePattern = getUnassembledPattern(state);
        if (assemblePattern.test(world, origin) && checkSpaceForAssembly(world, origin, state))
        {
            getAssembledPattern(state).placeBlocks(world, origin, origin);
            return true;
        }
        return false;
    }

    public void disassemble(World world, BlockPos controllerPos, BlockState state, BlockPos origin)
    {
        MultiblockUnassembledPattern pattern = getUnassembledPattern(state);
        pattern.placeBlocks(world, controllerPos, controllerPos);
    }

    protected boolean checkSpaceForAssembly(World world, BlockPos origin, BlockState state)
    {
        BigBlockPattern pattern = getAssembledPattern(state);
        BlockPos.Mutable mutable = origin.mutableCopy();
        for (var entry : pattern.entries())
        {
            mutable.set(origin, entry.key());

            if (world.getBlockState(mutable).isOf(structureBlock))
                return false;
        }
        return true;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this))
        {
            disassemble(world, pos, state, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
