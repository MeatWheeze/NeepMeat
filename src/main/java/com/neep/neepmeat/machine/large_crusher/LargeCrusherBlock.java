package com.neep.neepmeat.machine.large_crusher;

import com.google.common.collect.ImmutableMap;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import com.neep.neepmeat.util.ItemUtil;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class LargeCrusherBlock extends BigBlock<LargeCrusherStructureBlock> implements MeatlibBlock, BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private final String name;
    private final BlockItem blockItem;
    private final BigBlockPattern northPattern;

    private final Map<Direction, BigBlockPattern> patternMap;
    private final Map<Direction, VoxelShape> shapeMap;

    private final VoxelShape northShape =
            VoxelShapes.combine(VoxelShapes.union(
                    VoxelShapes.cuboid(-1, 0, -1, 2, 2, 2),
                    VoxelShapes.cuboid(-1, 2, -1, 2, 3, 1)),
            VoxelShapes.cuboid(-10 / 16f, 2, -0.75, 1 + 10 / 16f, 3, 0.75), BooleanBiFunction.ONLY_FIRST);

    public LargeCrusherBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.name = name;
        this.blockItem = itemSettings.getFactory().create(this, name, itemSettings);
        this.northPattern = BigBlockPattern.oddCylinder(1, 0, 1, getStructure().getDefaultState())
                .set(-1, 2, 0, getStructure().getDefaultState())
                .enableApi(-1, 2, 0, ItemStorage.SIDED)
                .set(-0, 2, 0, getStructure().getDefaultState())
                .enableApi(-0, 2, 0, ItemStorage.SIDED)
                .set(1, 2, 0, getStructure().getDefaultState())
                .enableApi(1, 2, 0, ItemStorage.SIDED)
                .set(-1, 2, -1, getStructure().getDefaultState())
                .enableApi(-1, 2, -1, ItemStorage.SIDED)
                .set(0, 2, -1, getStructure().getDefaultState())
                .enableApi(0, 2, -1, ItemStorage.SIDED)
                .set(1, 2, -1, getStructure().getDefaultState())
                .enableApi(1, 2, -1, ItemStorage.SIDED)
                .enableApi(-1, 1, 0, MotorisedBlock.LOOKUP)
                .enableApi(1, 1, 0, MotorisedBlock.LOOKUP)
        ;

        this.patternMap = ImmutableMap.of(
                Direction.NORTH, northPattern,
                Direction.EAST, northPattern.rotateY(90),
                Direction.SOUTH, northPattern.rotateY(180),
                Direction.WEST, northPattern.rotateY(270)
        );

        this.shapeMap = ImmutableMap.of(
                Direction.NORTH, MiscUtil.rotateShapeY(northShape, 0),
                Direction.EAST, MiscUtil.rotateShapeY(northShape, 90),
                Direction.SOUTH, MiscUtil.rotateShapeY(northShape, 180),
                Direction.WEST, MiscUtil.rotateShapeY(northShape, 270)
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return shapeMap.get(state.get(FACING));
    }

    @Override
    protected LargeCrusherStructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new LargeCrusherStructureBlock("large_crusher_structure", this, settings));
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
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this))
        {
            world.getBlockEntity(pos, NMBlockEntities.LARGE_CRUSHER).ifPresent(be ->
                    ItemUtil.scatterItems(world, pos, be.getInputStorage(null)));
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.LARGE_CRUSHER.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.LARGE_CRUSHER, (world1, pos, state1, blockEntity) -> blockEntity.serverTick((ServerWorld) world1), ((world1, pos, state1, blockEntity) -> blockEntity.clientTick()), world);
    }
}
