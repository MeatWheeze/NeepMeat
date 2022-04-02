package com.neep.neepmeat.block.redstone;

import com.neep.neepmeat.api.block.NMBlock;
import com.neep.neepmeat.blockentity.BigLeverBlockEntity;
import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class BigLeverBlock extends LeverBlock implements NMBlock, BlockEntityProvider
{
    static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 10.0, 12.0, 15.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 0.0, 12.0, 15.0, 6.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 1.0, 4.0, 16.0, 15.0, 12.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 1.0, 4.0, 6.0, 15.0, 12.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 1.0, 12.0, 8.0, 15.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 0.0, 4.0, 15.0, 8.0, 12.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 8.0, 1.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 8.0, 4.0, 15.0, 16.0, 12.0);

    protected String registryName;
    protected BlockItem blockItem;

    public BigLeverBlock(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.blockItem = new BaseBlockItem(this, registryName, 64, true);

    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((WallMountLocation)state.get(FACE)) {
            case FLOOR: {
                switch (state.get(FACING).getAxis()) {
                    case X: {
                        return FLOOR_X_AXIS_SHAPE;
                    }
                }
                return FLOOR_Z_AXIS_SHAPE;
            }
            case WALL: {
                switch (state.get(FACING)) {
                    case EAST: {
                        return EAST_WALL_SHAPE;
                    }
                    case WEST: {
                        return WEST_WALL_SHAPE;
                    }
                    case SOUTH: {
                        return SOUTH_WALL_SHAPE;
                    }
                }
                return NORTH_WALL_SHAPE;
            }
        }
        switch (state.get(FACING).getAxis()) {
            case X: {
                return CEILING_X_AXIS_SHAPE;
            }
        }
        return CEILING_Z_AXIS_SHAPE;
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new BigLeverBlockEntity(pos, state);
    }
}
