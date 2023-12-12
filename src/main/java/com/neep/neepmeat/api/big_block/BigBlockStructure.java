package com.neep.neepmeat.api.big_block;

import com.neep.meatlib.block.MeatlibBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class BigBlockStructure extends Block implements MeatlibBlock, BlockEntityProvider
{
    protected final BigBlock parent;
    private final String registryName;

    public BigBlockStructure(BigBlock parent, String registryName, Settings settings)
    {
        super(settings);
        this.parent = parent;
        this.registryName = registryName;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        if (world.getBlockEntity(pos) instanceof BigBlockStructureBlockEntity be)
        {
            return be.translateShape(parent.getOutlineShape(state, world, pos, context));
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(state.getBlock()))
        {
            // Remove the controller block and let it handle the destruction of the rest of the structure.
            if (world.getBlockEntity(pos) instanceof BigBlockStructureBlockEntity be)
            {
                world.breakBlock(be.getControllerPos(), false);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return parent.getPickStack(world, pos, state);
    }

    @Override
    protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state)
    {
        world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(parent.getDefaultState()));
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
    {
        // Prevent the model from being darkened.
        return 1;
    }

    @Nullable
    @Override
    public BigBlockStructureBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return parent.getBlockEntityType().instantiate(pos, state);
    }
}
