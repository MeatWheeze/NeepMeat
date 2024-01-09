package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BlockVolume;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CharnelPumpBlock extends BigBlock<CharnelPumpStructure> implements MeatlibBlock, BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private final BlockVolume volume = BlockVolume.oddCylinder(1, 0, 7);
    private final String name;

    public CharnelPumpBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        ItemRegistry.queue(new BaseBlockItem(this, name, itemSettings));
        this.name = name;
    }

    @Override
    protected CharnelPumpStructure registerStructureBlock()
    {
        return BlockRegistry.queue(new CharnelPumpStructure(this, FabricBlockSettings.copyOf(this)), "charnel_pump_structure");
    }

    @Override
    protected BlockVolume getVolume(BlockState blockState)
    {
        return volume;
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.fullCube();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CHARNEL_PUMP.instantiate(pos, state);
    }
}
