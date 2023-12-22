package com.neep.assembly.actuator;

import com.neep.assembly.AssemblyEntity;
import com.neep.assembly.block.IRail;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.util.LinearDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Random;

public class LinearRailBlock extends BaseFacingBlock implements IRail
{
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
        }
        else
        {
            Direction facing = state.get(FACING);
            BlockState frontState = world.getBlockState(pos.offset(facing));
            BlockState backState = world.getBlockState(pos.offset(facing.getOpposite()));
            if (backState.isOf(this) && backState.get(DIRECTION) == LinearDirection.FORWARDS)
            {
                world.setBlockState(pos, state.with(DIRECTION, LinearDirection.FORWARDS));
            }
            else
            {
                world.setBlockState(pos, state.with(DIRECTION, LinearDirection.BACKWARDS));
            }
//            if (world.getBlockState(fromPos).isOf(this))
//            {
//                    if ((pos.offset(facing).equals(fromPos) || pos.offset(facing.getOpposite()).equals(fromPos))
//                    && world.getBlockState(fromPos).get(DIRECTION) != state.get(DIRECTION)
//                    )
//                {
//                    world.setBlockState(pos, world.getBlockState(fromPos));
//                    System.out.println("update");
//                }
//            }
//                else
//                {
//                    BlockState newState = state.with(DIRECTION, LinearDirection.BACKWARDS);
//                    world.setBlockState(pos, newState);
//                }
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
//        if (!world.isClient && state.isOf(this))
//        {
//            BlockState newState;
//            if (state.get(DIRECTION) == LinearDirection.STOP)
//            {
//                newState = state.with(DIRECTION, LinearDirection.FORWARDS);
//            }
//            else
//            {
//                newState = state.with(DIRECTION, LinearDirection.STOP);
//            }
//            world.setBlockState(pos, newState);
//            propagateState(world, pos, newState);
//        }
        return ActionResult.PASS;
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
//        world.getBlockTickScheduler().schedule(pos, state.getBlock(), 8);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
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
