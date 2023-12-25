package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class TelevisionBlock extends BaseBlock
{
    public static final IntProperty ROTATION = Properties.ROTATION;

    public TelevisionBlock(String name, ItemSettings itemSettings, FabricBlockSettings settings)
    {
        super(name, itemSettings, settings);
        setDefaultState(getDefaultState().with(ROTATION, 0));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(ROTATION, MathHelper.floor((ctx.getPlayerYaw() * 16.0f / 360.0f) + 0.5) & 0xF);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ROTATION);
    }
}
