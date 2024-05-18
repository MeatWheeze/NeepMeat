package com.neep.meatlib.block.multi;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.block.BaseDummyBlock;
import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public abstract class TallerBlock extends BaseBlock
{
    public static final VoxelShape OUTLINE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    private final TallerBlock.Structure structureBlock;
    private final IntProperty heightProperty;
    private final int maxHeight;

    public TallerBlock(String registryName, IntProperty heightProperty, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.pistonBehavior(PistonBehavior.IGNORE));

        this.heightProperty = heightProperty;
        this.maxHeight = heightProperty.getValues().stream().max(Integer::compare).get() + 1;

        this.structureBlock = createStructure();
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

    public IntProperty getHeightProperty()
    {
        return heightProperty;
    }

    protected abstract Structure createStructure();

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + maxHeight, pos.getZ());

        if (!super.canPlaceAt(state, world, pos) || !world.isSpaceEmpty(box))
            return false;

        boolean valid = true;
        for (int i = 0; i < maxHeight; i++)
        {
            valid = valid && world.getBlockState(pos.up(i)).isAir();
        }

        return valid;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        for (int i = 1; i < maxHeight; ++i)
        {
            BlockPos upPos = pos.up(i);
            if (world.getBlockState(upPos).isAir())
            {
                world.setBlockState(pos.up(i), structureBlock.getState(state).with(heightProperty, i));
            }
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this))
        {
            for (int i = 1; i < maxHeight; ++i)
            {
                BlockPos upPos = pos.up(i);
                if (world.getBlockState(upPos).isOf(structureBlock))
                {
                    world.setBlockState(upPos, Blocks.AIR.getDefaultState());
                }
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
    {
        return 1;
    }
    
    public class Structure extends BaseDummyBlock implements MeatlibBlockExtension
    {

        public Structure(String registryName, Settings settings)
        {
            super(registryName, settings.pistonBehavior(PistonBehavior.IGNORE));
        }

        protected int getHeight(BlockState state)
        {
            return state.get(getHeightProperty());
        }

        protected BlockState getState(BlockState baseState)
        {
            return getDefaultState();
        }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
        {
            return TallerBlock.this.getOutlineShape(state, world, pos, context).offset(0, -1, 0);
        }

        @Override
        public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
        {
            BlockPos basePos = pos.down(getHeight(state));
            if (!newState.isOf(this) && world.getBlockState(basePos).isOf(TallerBlock.this))
            {
                world.setBlockState(basePos, Blocks.AIR.getDefaultState());
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }

        @Override
        public ItemConvertible dropsLike()
        {
            return TallerBlock.this;
        }

        @Override
        public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
        {
            return 1;
        }

        @Override
        public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
        {
            return TallerBlock.this.getPickStack(world, pos, state);
        }

        @Override
        protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state)
        {
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(TallerBlock.this.getDefaultState()));
        }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
        {
            super.appendProperties(builder);
            builder.add(heightProperty);
        }
    }
}
