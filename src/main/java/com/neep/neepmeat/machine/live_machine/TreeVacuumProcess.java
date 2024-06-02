package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.block.TreeVacuumBlock;
import com.neep.neepmeat.machine.live_machine.block.entity.TreeVacuumBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class TreeVacuumProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(
            LivingMachineComponents.TREE_VACUUM,
            LivingMachineComponents.ITEM_OUTPUT,
            LivingMachineComponents.MOTOR_PORT
    );

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.TREE_VACUUM, LivingMachineComponents.ITEM_OUTPUT, LivingMachineComponents.MOTOR_PORT).ifPresent(result ->
        {
            var vacuums = result.t1();
            var outputs = result.t2();
            var motors = result.t3();

            TreeVacuumBlockEntity vacuum = vacuums.iterator().next();
            World world = be.getWorld();

            Direction facing = vacuum.getCachedState().get(TreeVacuumBlock.FACING);
            BlockPos trunkPos = vacuum.getPos().offset(facing, 2);
            BlockState trunkState = world.getBlockState(trunkPos);

                if (world.getTime() % 20 == 0)
                {
                    try (Transaction transaction = Transaction.openOuter())
                    {
                        traverseTree(world, trunkPos, 200, 7, be.getCombinedItemOutput(), transaction);
                        vacuum.syncAnimation();
                        transaction.commit();
                    }
                }
        });
    }

    private void traverseTree(World world, BlockPos origin, int maxVisit, int maxBreak, Storage<ItemVariant> output, TransactionContext transaction)
    {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        if (!isTree(world.getBlockState(origin)))
            return;

        visited.add(origin);
        queue.add(origin);

        int treeBlocksVisited = 0;
        int treeBlocksBroken = 0;
        while (!queue.isEmpty()
                && treeBlocksVisited < maxVisit
                && treeBlocksBroken < maxBreak
        )
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            boolean foundOther = false;
            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);
                if (!visited.contains(mutable))
                {
                    visited.add(mutable.toImmutable());

                    BlockState offsetState = world.getBlockState(mutable);
                    if (isTree(offsetState))
                    {
                        foundOther = true;
                        treeBlocksVisited++;
                        queue.add(mutable.toImmutable());
                    }
                }
            }

            if (!foundOther)
            {

                List<ItemStack> dropped = Block.getDroppedStacks(world.getBlockState(current), (ServerWorld) world, current, world.getBlockEntity(current));
                for (var stack : dropped)
                {
                    output.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                }

                world.breakBlock(current, false);
                treeBlocksBroken++;
            }
        }
    }

    private static boolean isTree(BlockState state)
    {
        Block block = state.getBlock();
        return
                block instanceof LeavesBlock
                || state.isIn(BlockTags.LOGS)
                || state.isIn(BlockTags.LEAVES);
    }

    @Override
    public List<ComponentType<?>> getRequired()
    {
        return REQUIRED;
    }

    @Override
    public Text getName()
    {
        return Text.of("Tree Vacuum");
    }
}
