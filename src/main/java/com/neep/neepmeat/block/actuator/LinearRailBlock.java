package com.neep.neepmeat.block.actuator;

import com.neep.assembly.AssemblyEntity;
import com.neep.neepmeat.block.base.BaseFacingBlock;
import com.neep.neepmeat.util.LinearDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class LinearRailBlock extends BaseFacingBlock
{
    public static final EnumProperty<LinearDirection> DIRECTION = EnumProperty.of("direction", LinearDirection.class);

    public LinearRailBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(DIRECTION, LinearDirection.STOP));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos().offset(context.getSide().getOpposite()));
        if (state.isOf(this))
        {
            return this.getDefaultState().with(FACING, state.get(FACING));
        }
        return this.getDefaultState().with(FACING, context.getPlayer().isSneaking() ? context.getPlayerLookDirection() : context.getPlayerLookDirection().getOpposite());
    }

    @Deprecated
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        DebugInfoSender.sendNeighborUpdate(world, pos);
        if (world.isReceivingRedstonePower(pos))
        {
            BlockState newState = state.with(DIRECTION, LinearDirection.FORWARDS);
            world.setBlockState(pos, newState);
//            propagateState(world, pos, newState);
        }
        else
        {
            if (!world.getBlockState(pos.down()).isOf(this))
            {
                BlockState newState = state.with(DIRECTION, LinearDirection.BACKWARDS);
                world.setBlockState(pos, newState);
//                propagateState(world, pos, newState);
            }
            else
            {
                world.setBlockState(pos, world.getBlockState(pos.down()));
            }
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient && state.isOf(this))
        {
            BlockState newState;
            if (state.get(DIRECTION) == LinearDirection.STOP)
            {
                newState = state.with(DIRECTION, LinearDirection.FORWARDS);
            }
            else
            {
                newState = state.with(DIRECTION, LinearDirection.STOP);
            }
            world.setBlockState(pos, newState);
            propagateState(world, pos, newState);
        }
        return ActionResult.SUCCESS;
    }

    public static BlockPos propagateState(World world, BlockPos start, BlockState state)
    {
        LinearDirection dir = state.get(DIRECTION);
        BlockState state1 = state;
        BlockPos pos = start;
        int i = 1;
//        while (state1.getBlock() instanceof LinearRailBlock && state1.get(FACING) == state.get(FACING) && i < 20)
//        {
//            state1 = world.getBlockState(pos);
//            world.setBlockState(pos, state1.with(DIRECTION, dir));
//            pos = pos.down();
//            state1 = world.getBlockState(pos);
//            ++i;
//        }
        pos = start;
        while (state1.getBlock() instanceof LinearRailBlock && state1.get(FACING) == state.get(FACING) && i < 20)
        {
            state1 = world.getBlockState(pos);
            world.setBlockState(pos, state1.with(DIRECTION, dir));
            pos = pos.up();
            state1 = world.getBlockState(pos);
            ++i;
        }
        return pos;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        world.getBlockTickScheduler().schedule(pos, state.getBlock(), 8);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        System.out.println("tick");
        Vec3f vec = state.get(FACING).getUnitVector();
        LinearDirection dir = state.get(DIRECTION);
        Box box = new Box(pos, pos.add(1, 1, 1)).expand(vec.getX(), vec.getY(), vec.getZ());
        world.getEntitiesByType(TypeFilter.instanceOf(AssemblyEntity.class), box, entity -> true).forEach(assembly ->
        {
            System.out.println(assembly);
            System.out.println(vec);
            assembly.setVelocity(0, 0.1, 0);
        });
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING).add(DIRECTION);
    }
}
