package com.neep.neepmeat.machine.live_machine;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.api.live_machine.LivingMachineStructure;
import com.neep.neepmeat.api.live_machine.StructureProperty;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class LargeCompressorBlock extends BigBlock<BigBlockStructure.Simple<BigBlockStructureEntity>> implements MeatlibBlock, BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private final String registryName;
    private final Map<Direction, BigBlockPattern> patternMap;
    private final Map<Direction, VoxelShape> shapeMap;

    public LargeCompressorBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.registryName = registryName;

        ItemRegistry.queue(new BaseBlockItem(this, registryName, itemSettings));

        BigBlockPattern pattern = new BigBlockPattern().oddCylinder(1, 0, 2, () -> getStructure().getDefaultState());
//                .range(0, 2, 1, 0, 2, -1, () -> getStructure().getDefaultState());

        patternMap = new EnumMap<>(Direction.class);
        patternMap.put(Direction.NORTH, pattern);
        patternMap.put(Direction.EAST, pattern.rotateY(90));
        patternMap.put(Direction.SOUTH, pattern.rotateY(180));
        patternMap.put(Direction.WEST, pattern.rotateY(270));

        VoxelShape northShape =VoxelShapes.combineAndSimplify(
                Block.createCuboidShape(-16, 0, -16, 32, 16, 32),
                Block.createCuboidShape(-6, 16, -16, 22, 47, 32),
                BooleanBiFunction.OR);

        shapeMap = new EnumMap<>(Direction.class);
        shapeMap.put(Direction.NORTH, northShape);
        shapeMap.put(Direction.EAST, MiscUtil.rotateShapeY(northShape, 90));
        shapeMap.put(Direction.SOUTH, MiscUtil.rotateShapeY(northShape, 180));
        shapeMap.put(Direction.WEST, MiscUtil.rotateShapeY(northShape, 270));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected BigBlockStructure.Simple<BigBlockStructureEntity> registerStructureBlock()
    {
        // Oh, crumbs
        BigBlockStructure.BlockEntityRegisterererer<BigBlockStructureEntity> registerererer = b -> Registry.register(
                Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "large_compressor_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new BigBlockStructureEntity(b.getBlockEntityType(), p, s), this).build());

        return BlockRegistry.queue(new BigBlockStructure.Simple<>(this, FabricBlockSettings.copyOf(this), registerererer), "large_compressor_structure");
    }

    @Override
    public BigBlockPattern getVolume(BlockState blockState)
    {
        return patternMap.get(blockState.get(FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.combineAndSimplify(
                Block.createCuboidShape(-10, 0, -16, 26, 16, 32),
                Block.createCuboidShape(-6, 16, -16, 22, 47, 32),
                BooleanBiFunction.OR);
//        return shapeMap.get(state.get(FACING));
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
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
        return LivingMachines.LARGE_COMPRESSOR_BE.instantiate(pos, state);
    }

    public static class Structure extends BigBlockStructure<BigBlockStructureEntity> implements LivingMachineStructure
    {
        public Structure(BigBlock<?> parent, Settings settings, BlockEntityRegisterererer<BigBlockStructureEntity> registerBlockEntity)
        {
            super(parent, settings, registerBlockEntity);
        }

        @Override
        public EnumMap<StructureProperty, StructureProperty.Entry> getProperties()
        {
            return StructureProperty.EMPTY;
        }
    }
}
