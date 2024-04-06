package com.neep.neepmeat.api.live_machine;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import com.neep.neepmeat.machine.live_machine.block.entity.MotorPortBlockEntity;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class LivingMachineBlockEntity extends BlockEntity implements ComponentHolder
{
    protected List<LivingMachineStructure> structures = new ArrayList<>();
//    private final HashMultimap<ComponentType<?>, LivingMachineComponent> componentMap = HashMultimap.create();
    private final Collection<LivingMachineComponent>[] componentMap = (Collection<LivingMachineComponent>[]) Array.newInstance(Collection.class, ComponentType.Simple.NEXT_ID);
    private final BitSet currentComponents = new BitSet(); // Active components marked in one-hot codes
    private final EnumMap<StructureProperty, AtomicDouble> properties = new EnumMap<>(StructureProperty.class);

//    protected FailureManager failureManager = new Fa
    protected DegradationManager degradationManager = new DegradationManager(this::degradationRate, Random.create());
    private final float rateMultiplier = 1;

    protected long age = 0;
    protected int updateInterval = 80;
    protected int maxSize = 100;

    protected float power;
    protected float repairAmount;

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

//    private boolean counting = false;
//    private int countTicks = 0;
//    private FloatArrayList estimatedDegradation;
//    private long estimatedTicks;

    protected void tickDegradation()
    {
        float repairPerDroplet = 4 / 81000f; // Decrease in degradation from one droplet
        long maxConsume = 8;
        long consumePerTick = Math.min(MathHelper.ceil(degradationManager.getDegradation() / repairPerDroplet), maxConsume);

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

        repairAmount = 0;

        if (satisfied.get() >= consumePerTick)
        {
            repairAmount += rateMultiplier * repairPerDroplet * satisfied.get();
        }

        float selfRepair = getSelfRepair();
        if (selfRepair > 0)
        {
            repairAmount += rateMultiplier * selfRepair;
        }

        var motors1 = getComponent(LivingMachineComponents.MOTOR_PORT);

        // Debug counter
//        if (power > 0 && !motors1.isEmpty() && !counting && degradationManager.getDegradation() < 0.75)
//        {
//            counting = true;
//            estimatedDegradation = degradationManager.storeRul();
//            estimatedTicks = estimatedDegradation.size();
//            NeepMeat.LOGGER.info("Started counting");
//        }

        if (world.getTime() % 20 == 0 && !motors1.isEmpty())
        {
            NeepMeat.LOGGER.info("Efficiency: {}", 100 * getEfficiency());
        }

//        if (counting)
//        {
//            NeepMeat.LOGGER.info("True: {}, expected: {}", estimatedDegradation.getFloat(countTicks), degradationManager.getDegradation());
//        }

        degradationManager.tick();

//        if (counting)
//        {
//            if (degradationManager.getDegradation() >= 0.75 && counting)
//            {
//                NeepMeat.LOGGER.info("Took {} ticks to reach 25%. Expected: {} ticks", countTicks, estimatedTicks);
//                counting = false;
//            }
//            countTicks++;
//        }
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

        if (age % updateInterval == 0)
        {
            updateStructure();
        }
    }

//    public Multimap<ComponentType<?>, LivingMachineComponent> getComponents()
//    {
//        return componentMap;
//    }


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
        // Is this bad? Should I be using AtomicDouble and AtomicInteger in single-threaded code? Leave your answer in the comments.

        EnumMap<StructureProperty, AtomicInteger> present = new EnumMap<>(StructureProperty.class);
        for (var structure : structures)
        {
            structure.getProperties().forEach((property, entry) ->
            {
                if (entry.function().average())
                    present.computeIfAbsent(property, p -> new AtomicInteger(0)).incrementAndGet();
            });
        }

        properties.clear();
        for (var structure : structures)
        {
            structure.getProperties().forEach((property, entry) ->
            {
                var numberPresent = present.get(property);
                entry.apply(properties.computeIfAbsent(property, p -> new AtomicDouble(p.defaultValue())), numberPresent != null ? numberPresent.get() : 1);
            });
        }
    }

    protected float getProperty(StructureProperty property)
    {
        return properties.computeIfAbsent(property, p -> new AtomicDouble(p.defaultValue())).floatValue();
    }

    public <T extends LivingMachineComponent> Collection<T> getComponent(ComponentType<T> type)
    {
        int idx = type.getId();
        if (!currentComponents.get(idx))
            return Collections.emptySet();

        return (Collection<T>) componentMap[idx];
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
//        return getProperty(StructureProperty.SPEED)
//                * (1 - degradationManager.getDegradation());
        return  (1 - degradationManager.getDegradation());
    }

    public float getSelfRepair()
    {
        return getProperty(StructureProperty.SELF_REPAIR);
    }

    public float getRatedPower()
    {
        return getProperty(StructureProperty.MAX_POWER) / PowerUtils.referencePower();
    }

    public float degradationRate(float degradation)
    {
        float rate = -repairAmount;
        if (power <= getRatedPower() / 2)
        {
            return rate;
        }

        if (power > getRatedPower())
            rate += (float) (rateMultiplier * (0.0002f * power / getRatedPower()) * Math.pow(1 - degradation, 4));
        else
            rate += (float) (rateMultiplier * (0.0001f * Math.pow(1 - degradation, 4)));

        return rate;
    }

    public double getPower()
    {
        return power;
    }

    public double getRulHours()
    {
        return degradationManager.estimateRul() / (20 * 3600.0);
    }
}
