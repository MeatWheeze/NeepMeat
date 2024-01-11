package com.neep.neepmeat.machine.well_head;

import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WellHeadBlock extends TallBlock implements BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public WellHeadBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());
    }

    @Override
    protected Structure createStructure()
    {
        return BlockRegistry.queue(new Structure(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings))
        {
            @Override
            public BlockRenderType getRenderType(BlockState state)
            {
                return BlockRenderType.INVISIBLE;
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
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return super.getOutlineShape(state, world, pos, context);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.WELL_HEAD.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.WELL_HEAD, ((world1, pos, state1, blockEntity) -> blockEntity.serverTick()), null, world);
    }
}
