package com.neep.neepmeat.api.live_machine;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public abstract class LivingMachineBlockEntity extends BlockEntity
{
    protected List<LivingMachineStructure> structures = new ArrayList<>();
    protected long age = 0;
    protected int updateInterval = 80;
    protected int maxSize = 64;

    public LivingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.age = nbt.getLong("age");
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putLong("age", age);
    }

    public void serverTick()
    {
        age++;

        if (age % updateInterval == 0)
        {
            updateStructure();
        }
    }

    protected void updateStructure()
    {
        search(getPos());
        processStructure();
    }

    protected abstract void processStructure();

    protected void search(BlockPos start)
    {
        structures.clear();

        Set<BlockPos> visited = Sets.newHashSet();
        Queue<BlockPos> queue = Queues.newArrayDeque();
        visited.add(pos);

        queue.add(start);

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();
            BlockPos.Mutable mutable = current.mutableCopy();
            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);

                if (!visited.contains(mutable))
                {
                    BlockState nextState = world.getBlockState(mutable);

                    if (structures.size() >= maxSize)
                    {
                        return;
                    }

                    if (nextState.getBlock() instanceof LivingMachineStructure structure)
                    {
                        structures.add(structure);
                    }
                    else if (nextState.getBlock() instanceof LivingMachineBlock)
                    {
                        structures.clear();
                        return;
                    }
                    else if (world.getBlockEntity(mutable) instanceof LivingMachineComponent component)
                    {
                        component.setController(pos);
                    }
                }
            }
        }
    }
}
