package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import com.neep.neepmeat.util.MiscUtil;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class TreeVacuumBlock extends BigBlock<TreeVacuumBlock.Structure> implements MeatlibBlock, BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private final String registryName;
    private final Map<Direction, BigBlockPattern> patternMap;
    private final Map<Direction, VoxelShape> shapeMap;

    public TreeVacuumBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);

        this.registryName = name;
        ItemRegistry.queue(new BaseBlockItem(this, registryName, itemSettings));
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));

        BigBlockPattern volume = BigBlockPattern.makeRange(-1, 0, 0, 1, 0, 0, getStructure().getDefaultState())
                .set(0, 0, -1, getStructure().getDefaultState())
                .set(0, 1, 0, getStructure().getDefaultState())
                ;

        this.patternMap = new EnumMap<>(Direction.class);
        patternMap.put(Direction.NORTH, volume.rotateY(0));
        patternMap.put(Direction.EAST, volume.rotateY(90));
        patternMap.put(Direction.SOUTH, volume.rotateY(180));
        patternMap.put(Direction.WEST, volume.rotateY(270));

        VoxelShape northShape = VoxelShapes.union(
                Block.createCuboidShape(-8, 0, 0, 24, 16, 16),
                Block.createCuboidShape(0, 16, 0, 16, 24, 16),
                Block.createCuboidShape(0, 0, -16, 16, 16, 0)
        );

        this.shapeMap = new EnumMap<>(Direction.class);
        shapeMap.put(Direction.NORTH, MiscUtil.rotateShapeY(northShape, 0));
        shapeMap.put(Direction.EAST, MiscUtil.rotateShapeY(northShape, 90));
        shapeMap.put(Direction.SOUTH, MiscUtil.rotateShapeY(northShape, 180));
        shapeMap.put(Direction.WEST, MiscUtil.rotateShapeY(northShape, 270));
    }

    @Override
    protected TreeVacuumBlock.Structure registerStructureBlock()
    {
        BigBlockStructure.BlockEntityRegisterererer<BigBlockStructureEntity> register = b -> Registry.register(
                Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "tree_vacuum_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new BigBlockStructureEntity(b.getBlockEntityType(), p, s), this).build());

        return BlockRegistry.queue(new Structure(this, MeatlibBlockSettings.copyOf(settings), register), "tree_vacuum_structure");
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        if (ctx.getPlayer() == null)
            return getDefaultState();

        return getDefaultState().with(FACING, ctx.getPlayer().isSneaking() ? ctx.getHorizontalPlayerFacing().getOpposite() : ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected BigBlockPattern getVolume(BlockState blockState)
    {
        return patternMap.get(blockState.get(FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
//        if (state.get(FACING) == Direction.NORTH)
//        {
//            return VoxelShapes.union(
//                    Block.createCuboidShape(-8, 0, 0, 24, 16, 16),
//                    Block.createCuboidShape(0, 16, 0, 16, 24, 16),
//                    Block.createCuboidShape(0, 0, -16, 16, 16, 0)
//            );
//        }
        return shapeMap.get(state.get(FACING));
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
        return LivingMachines.TREE_VACUUM_BE.instantiate(pos, state);
    }

    public static class Structure extends BigBlockStructure<BigBlockStructureEntity>
    {
        public Structure(BigBlock<?> parent, Settings settings, BlockEntityRegisterererer<BigBlockStructureEntity> registerBlockEntity)
        {
            super(parent, settings, registerBlockEntity);
        }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
        {
            if (world.getBlockEntity(pos) instanceof BigBlockStructureEntity be)
            {
                BlockPos controllerPos = be.getControllerPos();
                if (controllerPos == null)
                    return VoxelShapes.fullCube();

                BlockState parentState = world.getBlockState(controllerPos);
                if (parentState.isOf(parent)) // Sometimes air replaces the parent (not sure why)
                    return be.translateChopShape(parent.getOutlineShape(parentState, world, pos, context));
            }

            return VoxelShapes.empty();
        }
    }
}
