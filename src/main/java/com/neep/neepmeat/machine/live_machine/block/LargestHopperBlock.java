package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import com.neep.neepmeat.machine.live_machine.block.entity.LargestHopperBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LargestHopperBlock extends BigBlock<LargestHopperBlock.StructureBlock> implements MeatlibBlock, BlockEntityProvider
{
    private final String registryName;
    private final BigBlockPattern pattern = new BigBlockPattern().oddCylinder(1, 0, 0, () -> getStructure().getDefaultState());
    private final VoxelShape shape = VoxelShapes.combine(VoxelShapes.cuboid(-1, 0, -1, 2, 1, 2),
            Block.createCuboidShape(-16 + 6, 2, -16 + 6, 32 - 6, 16, 32 - 6), BooleanBiFunction.ONLY_FIRST);

    public LargestHopperBlock(String registryName, Settings settings, ItemSettings itemSettings)
    {
        super(settings);
        itemSettings.create(this, registryName, itemSettings);
        this.registryName = registryName;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LivingMachines.LARGEST_HOPPER_BE.instantiate(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return shape;
    }

    @Override
    protected StructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new StructureBlock(this, FabricBlockSettings.copyOf(settings)), "largest_hopper_structure");
    }

    @Override
    protected BigBlockPattern getVolume(BlockState blockState)
    {
        return pattern;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        super.onEntityCollision(state, world, pos, entity);
        if (!world.isClient() && world.getTime() % 2 == 0 &&
                entity.isOnGround() &&
                entity instanceof ItemEntity itemEntity &&
                world.getBlockEntity(pos) instanceof LargestHopperBlockEntity be
        )
        {
            be.insertEntity(itemEntity);
        }
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockEntity(pos) instanceof LargestHopperBlockEntity be)
        {
            MeatlibStorageUtil.scatterNoTransaction(world, pos, be.getStorage(null));
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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
            return NMBlockEntities.register("largest_hopper_structure", (p, s) -> new StructureBlockEntity(getBlockEntityType(), p, s), this);
        }

        @Override
        public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
        {
            super.onEntityCollision(state, world, pos, entity);
            if (!world.isClient() && world.getTime() % 2 == 0 &&
                    entity.isOnGround() &&
                    entity instanceof ItemEntity itemEntity &&
                    world.getBlockEntity(pos) instanceof StructureBlockEntity be
            )
            {
                if (world.getBlockEntity(be.getControllerPos()) instanceof LargestHopperBlockEntity cbe)
                {
                    cbe.insertEntity(itemEntity);
                }
            }
        }

    }

    static class StructureBlockEntity extends BigBlockStructureEntity
    {
        public StructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
