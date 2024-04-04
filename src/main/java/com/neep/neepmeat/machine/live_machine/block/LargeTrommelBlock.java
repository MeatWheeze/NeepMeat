package com.neep.neepmeat.machine.live_machine.block;

import com.google.common.collect.ImmutableMap;
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
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class LargeTrommelBlock extends BigBlock<BigBlockStructure.Simple<LargeTrommelBlock.LargeTrommelStructureBlockEntity>> implements MeatlibBlock, BlockEntityProvider
{
    private final String registryName;

    private final Map<Direction, BigBlockPattern> patternMap;
    private final Map<Direction, VoxelShape> shapeMap;

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public LargeTrommelBlock(String registryName, Settings settings, ItemSettings itemSettings)
    {
        super(settings.nonOpaque());
        this.registryName = registryName;
        ItemRegistry.queue(new BaseBlockItem(this, registryName, itemSettings));
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));

        BigBlockPattern volume = BigBlockPattern.makeRange(0, 0, 0, 1, 1, -3, getStructure().getDefaultState());

        this.patternMap = ImmutableMap.of(
                Direction.NORTH, volume.rotateY(0),
                Direction.EAST, volume.rotateY(90),
                Direction.SOUTH, volume.rotateY(180),
                Direction.WEST, volume.rotateY(270)
        );

        VoxelShape northShape = volume.toVoxelShape();

        this.shapeMap = ImmutableMap.of(
                Direction.NORTH, MiscUtil.rotateShapeY(northShape, 0),
                Direction.EAST, MiscUtil.rotateShapeY(northShape, 90),
                Direction.SOUTH, MiscUtil.rotateShapeY(northShape, 180),
                Direction.WEST, MiscUtil.rotateShapeY(northShape, 270)
        );
    }

    @Override
    protected BigBlockStructure.Simple<LargeTrommelStructureBlockEntity> registerStructureBlock()
    {
        // Oh, crumbs
        BigBlockStructure.BlockEntityRegisterererer<LargeTrommelStructureBlockEntity> registerererer = b -> Registry.register(
                Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "large_trommel_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new LargeTrommelStructureBlockEntity(b.getBlockEntityType(), p, s), this).build());

            return BlockRegistry.queue(new BigBlockStructure.Simple<>(this, FabricBlockSettings.copyOf(this), registerererer), "large_trommel_structure");
    }

    @Override
    protected BigBlockPattern getVolume(BlockState blockState)
    {
        return patternMap.get(blockState.get(FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return shapeMap.get(state.get(FACING));
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
        if (!state.isOf(newState.getBlock()))
        {
            world.getBlockEntity(pos, NMBlockEntities.LARGE_MOTOR).ifPresent(MotorEntity::onRemoved);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LivingMachines.LARGE_TROMMEL_BE.instantiate(pos, state);
    }

//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
//    {
//        return MiscUtil.checkType(type, NMBlockEntities.LARGE_MOTOR, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
//    }
//
//    @Nullable
//    @Override
//    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
//    {
//        return NMBlockEntities.LARGE_MOTOR.instantiate(pos, state);
//    }

//    public static class LargeTrommelStructureBlock extends BigBlockStructure<LargeTrommelStructureBlockEntity>
//    {
//        public LargeTrommelStructureBlock(BigBlock<?> parent, Settings settings)
//        {
//            super(parent, settings);
//        }
//
//        @Override
//        protected BlockEntityType<LargeTrommelStructureBlockEntity> registerBlockEntity()
//        {
//            return null;
//        }
//    }

    public static class LargeTrommelStructureBlockEntity extends BigBlockStructureEntity
    {
        public LargeTrommelStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
