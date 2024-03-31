package com.neep.neepmeat.api.live_machine;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.*;

public abstract class LivingMachineBlockEntity extends BlockEntity
{
    protected List<LivingMachineStructure> structures = new ArrayList<>();
    private final Multimap<ComponentType<?>, LivingMachineComponent> componentMap = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    private final EnumMap<LivingMachineStructure.Property, AtomicDouble> properties = new EnumMap<>(LivingMachineStructure.Property.class);

//    protected FailureManager failureManager = new Fa
    protected DegradationManager degradationManager = new DegradationManager(this::degradationRate, Random.create());

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

        componentMap.entries().removeIf(e -> e.getValue().componentRemoved());

        degradationManager.tick();

        if (age % updateInterval == 0)
        {
            updateStructure();
        }
    }

    public Multimap<ComponentType<?>, LivingMachineComponent> getComponents()
    {
        return componentMap;
    }

    public <T extends LivingMachineComponent> Collection<T> getComponent(ComponentType<T> type)
    {
        return (Collection<T>) getComponents().get(type);
    }

    public boolean hasComponents(ComponentType<?>... types)
    {
        return getComponents().keys().containsAll(Arrays.asList(types));
//        for (var type : types)
//        {
//            if (!getComponents().containsKey(type))
//                return false;
//        }
//        return true;
    }

    protected void updateStructure()
    {
        search(getPos());
        processStructure();
    }

    protected void processStructure()
    {
        properties.clear();
        for (var structure : structures)
        {
            structure.getProperties().forEach((property, value) ->
            {
                properties.computeIfAbsent(property, p -> new AtomicDouble(0)).addAndGet(value);
            });
        }

        // Average all properties
        for (var value : properties.values())
        {
            value.set(value.get() / properties.size());
        }
    }

    protected void search(BlockPos start)
    {
        structures.clear();
        componentMap.clear();

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
                    visited.add(mutable.toImmutable());

                    BlockState nextState = world.getBlockState(mutable);
                    if (nextState.isAir())
                        continue;

                    LivingMachineComponent component;
                    if (structures.size() + componentMap.size() >= maxSize)
                        return;

                    if (nextState.getBlock() instanceof LivingMachineBlock)
                    {
                        structures.clear();
                        componentMap.clear();
                        return;
                    }

                    if (nextState.getBlock() instanceof LivingMachineStructure structure)
                    {
                        structures.add(structure);
                        queue.add(mutable.toImmutable());
                    }
                    else if ((component = LivingMachineComponent.LOOKUP.find(world, mutable, null)) != null)
                    {
                        component.setController(pos);
                        componentMap.put(component.getComponentType(), component);
                        queue.add(mutable.toImmutable());
                    }
                }
            }
        }
    }

    public float getEfficiency()
    {
        // Take into account performance degradation and block types
        return (float)
                properties.get(LivingMachineStructure.Property.EFFICIENCY).get()
                * degradationManager.getDegradation();
    }

    public float degradationRate()
    {
        return 0.1f * (1 - degradationManager.getDegradation());
    }
}
