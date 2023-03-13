package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class ToiletBlock extends BaseHorFacingBlock
{
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final VoxelShape OPEN_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
//    public static final VoxelShape CLOSED_SHAPE = makeClosedShape();

    public ToiletBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
        setDefaultState(getDefaultState().with(OPEN, false));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.isReceivingRedstonePower(pos) && state.get(OPEN))
        {
            // Find a living entity in the block above and teleport it one block below the toilet block.
            Box box = new Box(pos.up());
            List<LivingEntity> entityList = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), box, e -> true);
            if (entityList.size() > 0)
            {
                Vec3d downCorner = Vec3d.of(pos.down(3));
                LivingEntity entity = entityList.get(0);
                if (world.isSpaceEmpty(entity.getBoundingBox().offset(downCorner)))
                {
                    double y = pos.getY() - 1 - entity.getHeight();
                    entity.requestTeleport(pos.getX() + 0.5, y, pos.getZ() + 0.5);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1f, 1f);
                }
            }
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return super.getPlacementState(context).with(OPEN, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        world.setBlockState(pos, state.cycle(OPEN));
        world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return OPEN_SHAPE;
    }

    // Auto-generated
    public static VoxelShape makeOpenShape()
    {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.125, 0.8125, 0.375, 1.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.375, 0.0625, 0.875, 0.6875, 1.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.5, 0, 1, 0.8125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5, 0, 0.125, 0.8125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.5, 0, 0.875, 0.8125, 0.125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.828125, 0.875, 1, 1.765625, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.6875, 0.9375, 0.9375, 0.875, 1.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.03125, 1.125, 0.9375, 0.96875, 1.9375, 1.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.875, 0.96875, 0.875, 1.125, 1.40625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.8125, 0.84375, 0.875, 0.9375, 0.96875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.8125, 0.84375, 0.3125, 0.9375, 0.96875));

        return shape;
    }

    // Auto-generated
    public static VoxelShape makeClosedShape()
    {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.125, 0.8125, 0.375, 1.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.375, 0.0625, 0.875, 0.6875, 1.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.5, 0, 1, 0.8125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5, 0, 0.125, 0.8125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.5, 0, 0.875, 0.8125, 0.125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.828125, 0, 1, 0.890625, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.6875, 0.9375, 0.9375, 0.875, 1.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.03125, 1.125, 0.9375, 0.96875, 1.9375, 1.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.875, 0.96875, 0.875, 1.125, 1.40625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.8125, 0.84375, 0.875, 0.9375, 0.96875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.8125, 0.84375, 0.3125, 0.9375, 0.96875));

        return shape;
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(OPEN);
    }
}
