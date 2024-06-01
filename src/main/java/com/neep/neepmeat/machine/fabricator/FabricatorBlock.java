package com.neep.neepmeat.machine.fabricator;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FabricatorBlock extends TallBlock implements BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public FabricatorBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Override
    protected Structure createStructure()
    {
        return BlockRegistry.queue(new Structure(getRegistryName() + "_structure", MeatlibBlockSettings.copyOf(settings)));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        if (ctx.getPlayer() == null)
            return getDefaultState();

        return this.getDefaultState().with(FACING, ctx.getPlayer().isSneaking() ? ctx.getHorizontalPlayerFacing().getOpposite() : ctx.getHorizontalPlayerFacing());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FABRICATOR.instantiate(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }
}
