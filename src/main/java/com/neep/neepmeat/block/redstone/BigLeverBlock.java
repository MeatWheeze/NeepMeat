package com.neep.neepmeat.block.redstone;

import com.neep.neepmeat.api.block.NMBlock;
import com.neep.neepmeat.blockentity.BigLeverBlockEntity;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.SoundInitialiser;
import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BigLeverBlock extends LeverBlock implements NMBlock, BlockEntityProvider
{
    static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 10.0, 12.0, 15.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 0.0, 12.0, 15.0, 6.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 1.0, 4.0, 16.0, 15.0, 12.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 1.0, 4.0, 6.0, 15.0, 12.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 1.0, 12.0, 8.0, 15.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 0.0, 4.0, 15.0, 8.0, 12.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 8.0, 1.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 8.0, 4.0, 15.0, 16.0, 12.0);

    protected String registryName;
    protected BlockItem blockItem;

    public BigLeverBlock(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.blockItem = new BaseBlockItem(this, registryName, 64, true);

    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        switch (state.get(FACE))
        {
            case FLOOR:
            {
                switch (state.get(FACING).getAxis())
                {
                    case X:
                    {
                        return FLOOR_X_AXIS_SHAPE;
                    }
                }
                return FLOOR_Z_AXIS_SHAPE;
            }
            case WALL:
            {
                switch (state.get(FACING))
                {
                    case EAST:
                    {
                        return EAST_WALL_SHAPE;
                    }
                    case WEST:
                    {
                        return WEST_WALL_SHAPE;
                    }
                    case SOUTH:
                    {
                        return SOUTH_WALL_SHAPE;
                    }
                }
                return NORTH_WALL_SHAPE;
            }
        }
        switch (state.get(FACING).getAxis())
        {
            case X:
            {
                return CEILING_X_AXIS_SHAPE;
            }
        }
        return CEILING_Z_AXIS_SHAPE;
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
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
        return new BigLeverBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isSneaking())
        {
            BlockState blockState = this.togglePower(state, world, pos);
            ((BigLeverBlockEntity) world.getBlockEntity(pos)).togglePower();

        }
        else
        {
            BigLeverBlockEntity be = ((BigLeverBlockEntity) world.getBlockEntity(pos));
            if (be.activeTicks > 0)
                be.activeTicks = 0;
            else
                be.activeTicks = 40;

            System.out.println(be.activeTicks);

            world.playSound(null, pos, SoundInitialiser.CLICK, SoundCategory.BLOCKS, 0.7f, 2);

//            Direction facing = state.get(FACING);
//            Direction hitSide = hit.getSide();
//            if (hitSide == facing.rotateYClockwise() || hitSide == facing.rotateYCounterclockwise())
//            {
//                Vec3d hitPos = hit.getPos();
//                NMVec2f relative = NMMaths.removeAxis(hitSide.getAxis(), hitPos.subtract(pos.getX(), pos.getY(), pos.getZ()));
//                boolean increasing = relative.getX() > 0.25;
//                world.playSound(null, pos, SoundInitialiser.CLICK, SoundCategory.BLOCKS, 0.4f, increasing ? 2 : 1);
//            }
        }
        if (world.isClient)
            return ActionResult.SUCCESS;
        return ActionResult.CONSUME;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return checkType(type, BlockEntityInitialiser.BIG_LEVER, BigLeverBlockEntity::serverTick, world);
    }

    public void setPowered(World world, BlockPos pos, boolean powered)
    {
        BlockState state = world.getBlockState(pos).with(POWERED, powered);
        world.emitGameEvent(null, world.getBlockState(pos).get(POWERED) != powered ? GameEvent.BLOCK_SWITCH : GameEvent.BLOCK_UNSWITCH, pos);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);

        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(LeverBlock.getDirection(state).getOpposite()), this);

        if (powered)
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundInitialiser.BIG_LEVER_ON, SoundCategory.HOSTILE, 1f, 0.8f);
        else
            world.playSound(null, pos, SoundInitialiser.BIG_LEVER_OFF, SoundCategory.HOSTILE, 1f, 0.9f);

    }
}