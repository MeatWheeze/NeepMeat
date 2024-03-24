package com.neep.neepmeat.api.multiblock2;

import com.neep.neepmeat.api.big_block.BigBlockPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class Multiblock2ControllerBlock<T extends MultiBlockStructure<?>> extends Block
{
    private final T structureBlock;
    public static final BooleanProperty ASSEMBLED = BooleanProperty.of("assembled");

    public Multiblock2ControllerBlock(Settings settings)
    {
        super(settings);
        this.structureBlock = registerStructureBlock();
        setDefaultState(getDefaultState().with(ASSEMBLED, false));
    }

    protected abstract BigBlockPattern getAssembledPattern(BlockState blockState);
    protected abstract MultiblockUnassembledPattern getUnassembledPattern(BlockState blockState);

    protected abstract T registerStructureBlock();

    public T getStructure()
    {
        return structureBlock;
    }

    public VoxelShape getAssembledShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return getOutlineShape(state, world, pos, context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.isSneaking())
        {
            getUnassembledPattern(state).placeBlocks(world, pos, pos);
            return ActionResult.SUCCESS;
        }

        if (assemble(world, pos, state))
            return ActionResult.SUCCESS;

        return super.onUse(state, world, pos, player, hand, hit);
    }

    public boolean assemble(World world, BlockPos controllerPos, BlockState state)
    {
        MultiblockUnassembledPattern assemblePattern = getUnassembledPattern(state);
        if (assemblePattern.test(world, controllerPos) && checkSpaceForAssembly(world, controllerPos, state))
        {
            getAssembledPattern(state).placeBlocks(world, controllerPos, controllerPos);
            world.setBlockState(controllerPos, state.with(ASSEMBLED, true));
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
        if (!newState.isOf(this) && state.get(ASSEMBLED))
        {
            disassemble(world, pos, state, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ASSEMBLED);
    }
}
