package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MobPlatformBlock extends BaseBlock implements BlockEntityProvider
{
    protected static final VoxelShape SHAPE = makeShape();

    public MobPlatformBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        world.getBlockEntity(pos, NMBlockEntities.MOB_PLATFORM).ifPresent(be ->
        {
            MobEntity entity = be.captureLead(player);
            if (entity != null)
            {
                be.interact(entity);
            }
            else
            {
                be.ejectPassenger();
//                be.interact(player);
            }
        });
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    protected static VoxelShape makeShape()
    {
        VoxelShape shape = VoxelShapes.empty();
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.125, -0.125, 0.5625, 0.25, 0.4375));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.25, -0.125, 0.5625, 0.5625, 0));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.125, 0, 0.625, 0.1875, 1));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.125, 0.375, 0.375, 0.1875, 0.625));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.125, 0.5625, 0.5625, 0.25, 1.125));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.25, 1, 0.5625, 0.5625, 1.125));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.125, 0.4375, 1.125, 0.25, 0.5625));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(1, 0.25, 0.4375, 1.125, 0.5625, 0.5625));
//        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.125, 0.375, 1, 0.1875, 0.625));

        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.125, 0, 0.625, 0.1875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.125, 0.375, 0.375, 0.1875, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(1, 0.25, 0.4375, 1.125, 0.5625, 0.5625));

        return shape;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.MOB_PLATFORM.instantiate(pos, state);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity)
    {
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
//        if (entity instanceof LivingEntity livingEntity )
//        {
//            world.getBlockEntity(pos, NMBlockEntities.MOB_PLATFORM).ifPresent(be ->
//            {
//                if (entity instanceof LivingEntity livingEntity && !be.hasPassenger()) {
//                    be.interact(livingEntity);
//                }
//            });
//        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.MOB_PLATFORM, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }
}
