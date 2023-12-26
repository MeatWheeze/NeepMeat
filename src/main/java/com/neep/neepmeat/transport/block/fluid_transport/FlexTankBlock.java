package com.neep.neepmeat.transport.block.fluid_transport;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FlexTankBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class FlexTankBlock extends BaseBlock implements BlockEntityProvider
{
    public FlexTankBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName,itemSettings, settings);
    }

    protected static Set<FlexTankBlockEntity> findThings(World world, BlockPos start)
    {
        Set<FlexTankBlockEntity> found = Sets.newHashSet();
        Queue<BlockPos> queue = Queues.newArrayDeque();
        Set<BlockPos> visited = Sets.newHashSet();

        queue.add(start);
        visited.add(start);

        if (world.getBlockEntity(start) instanceof FlexTankBlockEntity be)
        {
            found.add(be);
        }

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);

                if (visited.contains(mutable))
                    continue;

                visited.add(mutable.toImmutable());

                if (world.getBlockEntity(mutable) instanceof FlexTankBlockEntity be)
                {
                    found.add(be);
                    queue.add(mutable.toImmutable());
                }
            }
        }

        return found;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (world.getBlockEntity(pos) instanceof FlexTankBlockEntity be)
        {
            updateConnections(world, be);
        }
    }

    protected static void updateConnections(World world, FlexTankBlockEntity origin)
    {
        var found = findThings(world, origin.getPos());

        FlexTankBlockEntity root = null;
        int lowest = Integer.MAX_VALUE;
        for (var be : found)
        {
            BlockPos bePos = be.getPos();
            if (bePos.getY() < lowest)
            {
                lowest = bePos.getY();
                root = be;
            }
        }

        root.markRoot(found.stream().map(BlockEntity::getPos).collect(Collectors.toSet()));

        for (var be : found)
        {
            be.setRoot(root);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (!world.isClient())
        {
            if (!newState.isOf(this))
            {
                BlockPos.Mutable mutable = pos.mutableCopy();
                Set<FlexTankBlockEntity> roots = Sets.newHashSet();
                Set<FlexTankBlockEntity> adjacent = Sets.newHashSet();
                for (Direction direction : Direction.values())
                {
                    mutable.set(pos, direction);

                    if (world.getBlockEntity(mutable) instanceof FlexTankBlockEntity be)
                    {
                        adjacent.add(be);
                        roots.add(be.getRoot());
                    }
                }

                for (FlexTankBlockEntity adj : adjacent)
                {
                    updateConnections(world, adj);
                }
            }
        }

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && world.getBlockEntity(pos) instanceof FlexTankBlockEntity be)
        {
            updateConnections(world, be);
        }
    }

    //    @Override
//    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
//    {
//        if (!world.isClient())
//        {
//            world.createAndScheduleBlockTick(pos, this, 1);
//        }
//
//        super.onBlockAdded(state, world, pos, oldState, notify);
//    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.getStackInHand(hand).isEmpty())
        {
            if (!world.isClient() && world.getBlockEntity(pos) instanceof FlexTankBlockEntity be)
            {
                player.sendMessage(Text.of(be.getRoot().getPos().toString() + " " + be.getSize()));

            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FLEX_TANK.instantiate(pos, state);
    }
}
