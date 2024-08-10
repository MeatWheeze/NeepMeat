package com.neep.neepmeat.machine.reactor;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.util.IterateRandomly;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class ReactionCoreBlockEntity extends SyncableBlockEntity
{
    private final ReactionCoreParameters parameters = new ReactionCoreParameters();
    private final Random random = Random.create();
    private int age = 0;

    private final Object2IntMap<ReceiverOrganismStructure> structures = new Object2IntOpenHashMap<>();

    private BlockPos lastOrigin;

    public double lerpIncidentZoneRadius;
    public double clientIncidentZoneRadius;

    private final Object2FloatMap<ReceiverOrganismStructure.Property> properties = new Object2FloatArrayMap<>();


    public ReactionCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.lastOrigin = pos;
    }

    public void serverTick()
    {
        ++age;

        if (age % 80 == 0)
        {
            updateStructure(getPos());
        }

        parameters.tick(0.09f, properties.getOrDefault(ReceiverOrganismStructure.Property.ORGANISATION, 0));

        if (world.getTime() % 10 == 0)
        {
            double stored = parameters.getStoredExudate();

            float factor = 1;

            // Convert amount to blocks
            int toPlace = (int) Math.floor(stored * factor);

            int placed = placeExudate(toPlace);

            // Convert blocks to amount
            double toExtract = placed / factor;

            parameters.extractStored(toExtract);
            sync();
        }

        parameters.tickIncidentZone();
    }

    private void updateStructure(BlockPos origin)
    {
        List<ReceiverOrganismStructure> structures = findStructures(world, origin);

        EnumMap<ReceiverOrganismStructure.Property, Integer> present = new EnumMap<>(ReceiverOrganismStructure.Property.class);
        for (var structure : structures)
        {
            structure.getProperties().forEach((property, value) ->
                    {
                        if (value.function().average())
                            present.compute(property, (p, v) -> v == null ? 1 : v + 1);
                    });
        }

        properties.clear();
        for (var structure : structures)
        {
            structure.getProperties().forEach((property, entry) ->
            {
                int numberPresent = present.getOrDefault(property, 1);
                properties.compute(property, (p, prev) ->
                {
                    if (prev == null)
                        prev = p.defaultValue();

                    return entry.apply(prev, numberPresent);
                });
            });
        }
    }

    private List<ReceiverOrganismStructure> findStructures(World world, BlockPos origin)
    {
        List< ReceiverOrganismStructure> structures = new ObjectArrayList<>();

        LongSet visited = new LongOpenHashSet();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(origin);
        visited.add(origin.asLong());

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);

                if (!visited.contains(mutable.asLong()))
                {
                    visited.add(mutable.asLong());

                    BlockState nextState = world.getBlockState(mutable);

                    if (nextState.getBlock() instanceof ReceiverOrganismStructure structure)
                    {
                        structures.add(structure);
                        queue.add(mutable.toImmutable());
                    }
                }
            }
        }

        return structures;
    }

    private int placeExudate(int toPlace)
    {
        int placed = placeExudateInIncidentZone(world, toPlace);

        if (placed < toPlace)
        {
            placed += extrudeExudate(world, toPlace - placed);
        }

        return placed;
    }

    // Randomly replaces blocks in the incident zone
    private int placeExudateInIncidentZone(World world, int toPlace)
    {
        int placed = 0;
        for (int i = 0; i < toPlace; ++i)
        {
            BlockPos randomPos = randomPosInSphere(pos, (float) parameters.getIncidentZoneRadius());

            BlockState prevState = world.getBlockState(randomPos);
            // TODO: tag
            if (!isValidBlock(prevState) && (prevState.isReplaceable() || random.nextBoolean()))
            {
                world.setBlockState(randomPos, IntrusionReactor.ACTIVE_WASTE.getDefaultState());
                ++placed;
            }
        }

        return placed;
    }

    private BlockPos randomPosInSphere(BlockPos origin, float radius)
    {
        // Random vector
        float rx = random.nextFloat() - 0.5f;
        float ry = random.nextFloat() - 0.5f;
        float rz = random.nextFloat() - 0.5f;

        float inverse = MathHelper.inverseSqrt(rx * rx + ry * ry + rz * rz);

        // Random length within incident zone
        float rl = inverse * random.nextFloat() * radius;

        // Normalise and extend to new length
        int x = origin.getX() + Math.round(rx * rl);
        int y = origin.getY() + Math.round(ry * rl);
        int z = origin.getZ() + Math.round(rz * rl);

        return new BlockPos(x, y, z);
    }

    // Uses a BFS to place exudate blocks in air spaces
    private int extrudeExudate(World world, int toPlace)
    {
        List<BlockPos> potential = traverse(world, lastOrigin, 800, toPlace);

        int canPlace = Math.min(potential.size(), toPlace);
        for (int i = 0; i < canPlace; ++i)
        {
            BlockPos pos = potential.get(potential.size() - i - 1);

            world.setBlockState(pos, IntrusionReactor.ACTIVE_WASTE.getDefaultState());
        }

        if (canPlace == 0)
        {
            lastOrigin = pos;
        }
        else
        {
            if (!lastOrigin.equals(pos))
                lastOrigin = pos;
            else
                lastOrigin = potential.get(potential.size() - 1);
        }

        return canPlace;
    }

    private List<BlockPos> traverse(World world, BlockPos origin, int maxVisit, int maxToPlace)
    {
        // Climb down more?
        maxToPlace *= 2;

        Direction[] horDirections = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};

        LongSet visited = new LongOpenHashSet();
        Queue<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> positions = new ArrayList<>();

        if (!isValidBlock(world.getBlockState(origin)))
            return List.of();

        visited.add(origin.asLong());
        queue.add(origin);

        int blocksVisited = 0;
        while (!queue.isEmpty()
                && blocksVisited < maxVisit
                && positions.size() < maxToPlace
        )
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            for (int i : new IterateRandomly(horDirections.length))
            {
                Direction direction = horDirections[i];

                mutable.set(current, direction);
                if (!visited.contains(mutable.asLong()))
                {
                    visited.add(mutable.asLong());

                    BlockState offsetState = world.getBlockState(mutable);
//                    if (direction != Direction.UP && offsetState.isAir())
                    if (offsetState.isAir())
                    {
                        positions.add(mutable.toImmutable());
                    }
                    else if (isValidBlock(offsetState))
                    {
                        blocksVisited++;
                        queue.add(mutable.toImmutable());
                    }
                }
            }
        }
        return positions;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("age", age);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.age = nbt.getInt("age");
    }

    @Override
    public void toClientTag(NbtCompound nbt)
    {
        super.toClientTag(nbt);
        nbt.putDouble("incident_zone_radius", parameters.getIncidentZoneRadius());
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        super.fromClientTag(nbt);
        this.clientIncidentZoneRadius = nbt.getDouble("incident_zone_radius");
    }

    private boolean isValidBlock(BlockState blockState)
    {
        return blockState.isOf(IntrusionReactor.ACTIVE_WASTE)
                || blockState.isOf(IntrusionReactor.REACTION_CORE)
                || blockState.getBlock() instanceof ReceiverOrganismStructure
                ;
    }
}
