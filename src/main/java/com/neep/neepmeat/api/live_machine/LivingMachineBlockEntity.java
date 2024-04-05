package com.neep.neepmeat.api.live_machine;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import com.neep.neepmeat.machine.live_machine.block.entity.MotorPortBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class LivingMachineBlockEntity extends BlockEntity implements ComponentHolder
{
    protected List<LivingMachineStructure> structures = new ArrayList<>();
//    private final HashMultimap<ComponentType<?>, LivingMachineComponent> componentMap = HashMultimap.create();
    private final Collection<LivingMachineComponent>[] componentMap = (Collection<LivingMachineComponent>[]) Array.newInstance(Collection.class, ComponentType.Simple.NEXT_ID);
    private final BitSet currentComponents = new BitSet();
    private final EnumMap<LivingMachineStructure.Property, AtomicDouble> properties = new EnumMap<>(LivingMachineStructure.Property.class);

//    protected FailureManager failureManager = new Fa
    protected DegradationManager degradationManager = new DegradationManager(this::degradationRate, Random.create());

    protected long age = 0;
    protected int updateInterval = 80;
    protected int maxSize = 64;

    protected float power;

    public LivingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.age = nbt.getLong("age");
        degradationManager.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putLong("age", age);
        degradationManager.writeNbt(nbt);
    }

    protected void tickDegradation()
    {
        float repairPerDroplet = 4 / 81000f; // Decrease in degradation from one droplet
        long maxConsume = 8;
        long consumePerTick = Math.min((long) (degradationManager.getDegradation() / repairPerDroplet), maxConsume);

//        long consumePerTick = 2; // 2d per tick will consume approximately one bucket per half hour
        AtomicLong satisfied = new AtomicLong();
        withComponents(LivingMachineComponents.INTEGRATION_PORT).ifPresent(w ->
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                for (var port : w.t1())
                {
                    if (satisfied.get() < consumePerTick)
                        satisfied.addAndGet(port.getFluidStorage(null).extract(FluidVariant.of(NMFluids.STILL_WORK_FLUID),
                                consumePerTick - satisfied.get(), transaction));
                    else
                        break;
                }
                transaction.commit();
            }
        });

        if (satisfied.get() >= consumePerTick)
        {
            degradationManager.subtract(repairPerDroplet * satisfied.get());
        }

        degradationManager.tick();

        var motors1 = getComponent(LivingMachineComponents.MOTOR_PORT);
        if (world.getTime() % 20 == 0 && !motors1.isEmpty())
        {
            NeepMeat.LOGGER.info("Efficiency: {}", 100 * getEfficiency());
        }
    }

    public void serverTick()
    {
        age++;
        tickDegradation();

        Collection<MotorPortBlockEntity> motors = getComponent(LivingMachineComponents.MOTOR_PORT);
        float nextPower = 0;
        if (!motors.isEmpty())
        {
            for (var motor : motors)
            {
                nextPower = Math.max(nextPower, motor.getPower());
            }
        }
        else
        {
            nextPower = 0;
        }
        power = nextPower;

//        for (var thing : componentMap)
//        {
//            if (thing != null)
//            {
//
//            }
//        }

        int i = currentComponents.nextSetBit(0);
        while (i != -1 && i < currentComponents.length())
        {
            i = currentComponents.nextSetBit(i);

            for (var component : componentMap[i])
            {
                if (component.componentRemoved())
                {
                    removeComponent(component);
                }
                else
                {
                    if (component instanceof CrusherSegmentBlockEntity be)
                    {
                        be.setProgressIncrement(getProgressIncrement());
                    }
                }
            }

            ++i;
        }

//        for (var it = componentMap.values().iterator(); it.hasNext();)
//        {
//            var component = it.next();
//            if (component.componentRemoved())
//            {
//                it.remove();
//            }
//            else
//            {
//                if (component instanceof CrusherSegmentBlockEntity be)
//                {
//                    be.setProgressIncrement(getProgressIncrement());
//                }
//            }
//        }

        degradationManager.tick();

        if (age % updateInterval == 0)
        {
            updateStructure();
        }
    }

//    public Multimap<ComponentType<?>, LivingMachineComponent> getComponents()
//    {
//        return componentMap;
//    }

    public <T extends LivingMachineComponent> Collection<T> getComponent(ComponentType<T> type)
    {
        int idx = type.getId();
        if (!currentComponents.get(idx))
            return Collections.emptySet();

        return (Collection<T>) componentMap[idx];
    }

//    public boolean hasComponents(ComponentType<?>... types)
//    {
//        return getComponents().keys().containsAll(Arrays.asList(types));
//    }

    protected void updateStructure()
    {
        search(getPos());
        processStructure();
    }

    protected void processStructure()
    {
        for (var structure : structures)
        {
            structure.getProperties().forEach((property, value) ->
            {
                properties.computeIfAbsent(property, p -> new AtomicDouble(0)).addAndGet(value);
            });
        }

        // Average all properties
//        for (var value : properties.values())
//        {
//            value.set(value.get() / properties.size());
//        }
    }

    protected double getProperty(LivingMachineStructure.Property property)
    {
        return properties.computeIfAbsent(property, p -> new AtomicDouble(1)).get();
    }

    protected void addComponent(LivingMachineComponent component)
    {
        int idx = component.getComponentType().getId();

        if (componentMap[idx] == null)
            componentMap[idx] = new HashSet<>();

        currentComponents.set(idx);
        componentMap[idx].add(component);
    }

    protected void removeComponent(LivingMachineComponent component)
    {
        int idx = component.getComponentType().getId();
        if (!currentComponents.get(idx))
            return;

        currentComponents.clear(idx);
        componentMap[idx].remove(component);
    }

    protected void clearComponents()
    {
        int i = currentComponents.nextSetBit(0);
        while (i != -1 && i <currentComponents.length())
        {
            i = currentComponents.nextSetBit(i);

            currentComponents.clear(i);
            var component = componentMap[i];
            if (component == null)
                NeepMeat.LOGGER.error("Something has gone horribly wrong");
            else
                componentMap[i].clear();

            ++i;
        }
    }

    protected void search(BlockPos start)
    {
        structures.clear();
        clearComponents();

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
                    if (structures.size() + currentComponents.stream().sum() >= maxSize)
                        return;

                    if (nextState.getBlock() instanceof LivingMachineBlock)
                    {
                        structures.clear();
                        clearComponents();
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
                        addComponent(component);
                        queue.add(mutable.toImmutable());
                    }
                }
            }
        }
    }

    public float getProgressIncrement()
    {
        return power * getEfficiency();
    }

    public float getEfficiency()
    {
        // Take into account performance degradation and block types
        return (float)
                getProperty(LivingMachineStructure.Property.SPEED)
                * (1 - degradationManager.getDegradation());
    }

    public float degradationRate()
    {
        return (float) (0.0001f * Math.pow(1 - degradationManager.getDegradation(), 4));
    }
}
