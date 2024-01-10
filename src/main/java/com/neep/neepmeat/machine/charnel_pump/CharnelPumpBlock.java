package com.neep.neepmeat.machine.charnel_pump;

import com.google.common.collect.ImmutableMap;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CharnelPumpBlock extends BigBlock<CharnelPumpStructure> implements MeatlibBlock, BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private final String name;

    private final BigBlockPattern volume;
    private final Map<Direction, BigBlockPattern> patternMap;

    public CharnelPumpBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        ItemRegistry.queue(new BaseBlockItem(this, name, itemSettings));
        this.name = name;
        volume = BigBlockPattern.oddCylinder(1, 0, 7, getStructure().getDefaultState())
                .set(2, 0, 0, getStructure().getDefaultState())
                .set(2, 1, 0, getStructure().getDefaultState())
                .enableApi(2, 1, 0, BloodAcceptor.SIDED);

        patternMap = ImmutableMap.of(
                Direction.NORTH, volume,
                Direction.EAST, volume.rotateY(90),
                Direction.SOUTH, volume.rotateY(180),
                Direction.WEST, volume.rotateY(270)
        );
    }

    @Override
    protected CharnelPumpStructure registerStructureBlock()
    {
        return BlockRegistry.queue(new CharnelPumpStructure(this, FabricBlockSettings.copyOf(this)), "charnel_pump_structure");
    }

    @Override
    protected BigBlockPattern getVolume(BlockState blockState)
    {
        return patternMap.get(blockState.get(FACING));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
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
