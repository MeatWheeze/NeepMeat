package com.neep.neepmeat.machine.reactor.disruptor;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.reactor.IntrusionReactor;
import kotlin.OptionalExpectation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class DisruptorSegmentBlock extends BigBlock<DisruptorSegmentBlock.StructureBlock> implements BlockEntityProvider
{
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    private final EnumMap<Direction.Axis, BigBlockPattern> patternMap;

    public DisruptorSegmentBlock(RegistrationContext ctx, Settings settings, ItemSettings itemSettings)
    {
        super(ctx, settings);
        itemSettings.create(this, ctx, itemSettings);

        patternMap = new EnumMap<>(Map.of(
                Direction.Axis.Y, BigBlockPattern.makeOddCylinder(1, 0, 0, getStructure().getDefaultState()),
                Direction.Axis.X, BigBlockPattern.makeRange(0, -1, -1, 0, 1, 1, getStructure().getDefaultState()),
                Direction.Axis.Z, BigBlockPattern.makeRange(-1, -1, 0, 1, 1, 0, getStructure().getDefaultState())
        ));
    }

    @Override
    protected StructureBlock registerStructureBlock(RegistrationContext ctx)
    {
        return ctx.append(this, new StructureBlock(this, MeatlibBlockSettings.copyOf(settings)), "disruptor_segment_structure");
    }

    @Override
    public BigBlockPattern getVolume(BlockState blockState)
    {
        return patternMap.get(blockState.get(AXIS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(AXIS);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        EnumMap<Direction.Axis, VoxelShape> shapeMap = new EnumMap<>(Map.of(
                Direction.Axis.Y, VoxelShapes.combine(
                        Block.createCuboidShape(-8, 0, -16, 24, 16, 32),
                        Block.createCuboidShape(-16, 0, -8, 32, 16, 24),
                        BooleanBiFunction.OR),
                Direction.Axis.X, VoxelShapes.combine(
                        Block.createCuboidShape(0, -8, -16, 16, 24, 32),
                        Block.createCuboidShape(0, -16, -8, 16, 32, 24),
                        BooleanBiFunction.OR),
                Direction.Axis.Z, VoxelShapes.combine(
                        Block.createCuboidShape(-8, -16, 0, 24, 32, 16),
                        Block.createCuboidShape(-16, -8, 0, 32, 24, 16),
                        BooleanBiFunction.OR)
        ));

        return shapeMap.get(state.get(AXIS));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(AXIS, ctx.getSide().getAxis());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return IntrusionReactor.DISRUPTOR_SEGMENT_BE.instantiate(pos, state);
    }

    public static class StructureBlock extends BigBlockStructure<StructureBlockEntity>
    {
        public StructureBlock(BigBlock<?> parent, Settings settings)
        {
            super(parent, settings);
        }

        @Override
        protected BlockEntityType<StructureBlockEntity> registerBlockEntity()
        {
            return NMBlockEntities.register("disruptor_segment_structure", (p, s) -> new StructureBlockEntity(getBlockEntityType(), p, s), this);
        }
    }

    public static class StructureBlockEntity extends BigBlockStructureEntity
    {
        public StructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
