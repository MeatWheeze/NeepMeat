package com.neep.meatlib.block.multi;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.block.BaseDummyBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.machine.mixer.MixerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public abstract class TallBlock extends BaseBlock
{
    public static final VoxelShape OUTLINE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    private final Block structureBlock;

    public TallBlock(String registryName, Settings settings)
    {
        this(registryName, ItemSettings.block(), settings);
//        super(registryName, settings);
//        this.structureBlock = getStructure();
    }

    public TallBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
//        super(registryName, itemMaxStack, tooltipSupplier, settings);
        this.structureBlock = getStructure();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return OUTLINE;
    }

    public final Block getStructureBlock()
    {
        return structureBlock;
    }

    protected abstract Structure getStructure();

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ());
        return world.getBlockState(pos.up()).isAir() && world.isSpaceEmpty(box) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        if (world.getBlockState(pos.up()).isAir())
        {
            world.setBlockState(pos.up(), structureBlock.getDefaultState());
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockState(pos.up()).isOf(structureBlock))
        {
            if (world.getBlockEntity(pos) instanceof MixerBlockEntity be)
            {
                be.dropItems();
            }
            world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
    {
        return 1;
    }
    
    public class Structure extends BaseDummyBlock
    {
        public Structure(String registryName, Settings settings)
        {
            super(registryName, settings);
        }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
        {
            return TallBlock.this.getOutlineShape(state, world, pos, context).offset(0, -1, 0);
        }

        @Override
        public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
        {
            if (!newState.isOf(this) && world.getBlockState(pos.down()).isOf(TallBlock.this))
            {
                if (world.getBlockEntity(pos.down()) instanceof MixerBlockEntity be)
                {
                    be.dropItems();
                }
                world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }

        @Override
        public ItemConvertible dropsLike()
        {
            return TallBlock.this;
        }

        @Override
        public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
        {
            return 1;
        }

        @Override
        public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
        {
            return TallBlock.this.getPickStack(world, pos, state);
        }

        @Override
        protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state)
        {
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(TallBlock.this.getDefaultState()));
        }
    }
}
