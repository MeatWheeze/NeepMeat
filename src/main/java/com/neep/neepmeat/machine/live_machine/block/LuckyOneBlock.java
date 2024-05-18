package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.block.multi.TallerBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class LuckyOneBlock extends TallerBlock implements BlockEntityProvider
{
    public static final IntProperty HEIGHT_3 = IntProperty.of("height", 1, 2);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public LuckyOneBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, HEIGHT_3, itemSettings, settings);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.fullCube();
    }

    @Override
    protected Structure createStructure()
    {
        return BlockRegistry.queue(new Structure(getRegistryName() + "_structure", MeatlibBlockSettings.copyOf(settings))
        {
            @Override
            protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
            {
                super.appendProperties(builder);
                builder.add(FACING);
            }

            @Override
            protected BlockState getState(BlockState baseState)
            {
                return super.getState(baseState).with(FACING, baseState.get(FACING));
            }

            @Override
            public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
            {
                return VoxelShapes.fullCube();
            }
        });
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LivingMachines.LUCKY_ONE_BE.instantiate(pos, state);
    }
}
