package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

public class BaseFacingBlock extends FacingBlock implements MeatlibBlock
{
    private final BlockItem blockItem;
    private final String registryName;

    public BaseFacingBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.getFactory().create(this, itemName, itemSettings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        this.registryName = itemName;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        if (context.getPlayer() == null)
            return getDefaultState();

        return this.getDefaultState().with(FACING, context.getPlayer().isSneaking() ? context.getPlayerLookDirection().getOpposite() : context.getPlayerLookDirection());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
