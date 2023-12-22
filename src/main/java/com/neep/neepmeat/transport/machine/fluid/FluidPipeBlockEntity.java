package com.neep.neepmeat.transport.machine.fluid;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.FluidPipe;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import com.neep.neepmeat.transport.fluid_network.node.BlockPipeVertex;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class FluidPipeBlockEntity<T extends PipeVertex & NbtSerialisable> extends BlockEntity
{
    protected boolean replaced = false;
    public NbtCompound queuedNbt;
    protected final T vertex;
    protected final PipeConstructor<T> constructor;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state, PipeConstructor<T> constructor)
    {
        this(NMBlockEntities.FLUID_PIPE, pos, state, constructor);
    }

    public FluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PipeConstructor<T> constructor)
    {
        super(type, pos, state);
        this.vertex = constructor.create(this);
        this.constructor = constructor;
    }

    public static Optional<FluidPipeBlockEntity<?>> find(World world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof FluidPipeBlockEntity<?> be)
        {
            return Optional.of(be);
        }
        return Optional.empty();
    }

    public void updateAdjacent(BlockState newState, Direction fromDirection)
    {
//        FluidPipe pipe = (FluidPipe) getCachedState().getBlock();
        updateAdjacent(newState);
    }

    public void updateAdjacent(BlockState newState)
    {
        FluidPipe pipe = (FluidPipe) newState.getBlock();

        vertex.reset();
        vertex.updateNodes((ServerWorld) world, pos, newState);

        int connections = pipe.countConnections(newState);

        if (connections > 2 || !vertex.canSimplify())
        {
            findAdjacent(pipe);
        }
        else if (connections == 2)
        {
            linkVertices(pipe);
        }
    }

    private void jankParticles(PipeVertex vertex)
    {
        if (vertex instanceof BlockPipeVertex bpv && world instanceof ServerWorld serverWorld)
        {
            BlockPos particlePos = BlockPos.fromLong(bpv.getPos());

            serverWorld.spawnParticles(ParticleTypes.COMPOSTER,
                    particlePos.getX() + 0.5, particlePos.getY() + 0.5, particlePos.getZ() + 0.5,
                    10, 0.25, 0.25, 0.25, 0.01);
        }
    }

    // For use if this pipe is new and may connect two vertices.
    private void linkVertices(FluidPipe pipe)
    {
        List<Pair<PipeVertex, Direction>> toConnect = Lists.newArrayList();
        for (Direction direction : pipe.getConnections(getCachedState(), direction -> true))
        {
            var nextVertex = findNextVertex(pos, direction);
            if (nextVertex != null)
            {
                toConnect.add(nextVertex);
            }
        }

        // No vertices found to link
        if (toConnect.size() != 2)
            return;

        var vertex1 = toConnect.get(0);
        var vertex2 = toConnect.get(1);

        vertex1.first().setAdjVertex(vertex1.second().getId(), vertex2.first());
        vertex2.first().setAdjVertex(vertex2.second().getId(), vertex1.first());

//        jankParticles(vertex1.first());
//        jankParticles(vertex2.first());
    }

    // For use if this pipe is a vertex. Performs a BFS at each connection to find the closest vertices.
    private void findAdjacent(FluidPipe pipe)
    {
        for (Direction direction : pipe.getConnections(getCachedState(), direction -> true))
        {
            var adjacent = findNextVertex(pos, direction);
            if (adjacent != null)
            {
                vertex.setAdjVertex(direction.getId(), adjacent.first());
                adjacent.first().setAdjVertex(adjacent.second().getId(), vertex);
            }
        }
    }

    private Pair<PipeVertex, Direction> findNextVertex(BlockPos pos, Direction out)
    {
        Set<BlockPos> visited = Sets.newHashSet(); // TODO: convert to long set
        Queue<Pair<BlockPos, Direction>> queue = Queues.newArrayDeque();

        queue.add(Pair.of(pos.offset(out), out));
        visited.add(pos.offset(out));
        visited.add(pos);

        while (!queue.isEmpty())
        {
            var current = queue.poll();
            BlockState currentState = world.getBlockState(current.first());
            FluidPipe pipe = FluidPipe.findFluidPipe(world, current.first(), currentState).orElse(null);

            // This indicates that the connection is to a node, so no further action is required.
            if (pipe == null)
                return null;

            int connections = pipe.countConnections(currentState);

            PipeVertex vertex = PipeVertex.LOOKUP.find(world, current.first(), null);

            if (vertex == null)
                throw new IllegalStateException("Fluid pipe does not have valid vertex lookup");

            if (connections > 2 || !vertex.canSimplify())
            {
//                jankParticles(vertex);
                return Pair.of(vertex, current.second().getOpposite());
            }

            BlockPos.Mutable offsetPos = current.first().mutableCopy();
            for (Direction direction : pipe.getConnections(currentState, d -> true))
            {
                offsetPos.set(current.first(), direction);

                if (!visited.contains(offsetPos))
                {
                    PipeVertex offsetVertex = PipeVertex.LOOKUP.find(world, offsetPos, null);

                    if (offsetVertex == null)
                        continue;

                    visited.add(offsetPos.toImmutable());
                    queue.add(Pair.of(offsetPos.toImmutable(), direction));
                }
            }
        }

        return null;
    }

    @Override
    public void markDirty()
    {
        world.markDirty(pos);
    }

    @Override
    public void setCachedState(BlockState state)
    {
//        markReplaced();
        super.setCachedState(state);
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
        if (getWorld() instanceof ServerWorld serverWorld && world.getServer().isOnThread())
        {
            if (queuedNbt != null)
            {
                FluidNodeManager.getInstance(getWorld()).readNodes(getPos(), queuedNbt, serverWorld);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt = FluidNodeManager.getInstance(getWorld()).writeNodes(getPos(), nbt);

        nbt.put("vertex", vertex.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        queuedNbt = nbt.copy();

        vertex.readNbt(nbt.getCompound("vertex"));
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
    }


    public void markReplaced()
    {
        replaced = true;
    }

    public boolean isCreatedDynamically()
    {
        return false;
    }

    public T getPipeVertex()
    {
        return vertex;
    }

    public void onUnload(ServerWorld world)
    {
        FluidNodeManager.getInstance(world).entityUnloaded(getPos());
    }

    public void onLoad(ServerWorld world)
    {
    }
    public void onRemove(ServerWorld world)
    {
        vertex.erase();
        FluidNodeManager.getInstance(world).entityRemoved(getPos());

    }

    @FunctionalInterface
    public interface PipeConstructor<T extends PipeVertex & NbtSerialisable>
    {
        T create(FluidPipeBlockEntity<T> parent);
    }

    static
    {
        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) ->
        {
            if (blockEntity instanceof FluidPipeBlockEntity<?> be)
            {
                if (!be.replaced)
                {
                    be.onUnload(world);
                }
                else
                {
                    be.onRemove(world);
                    be.replaced = false;
                }
            }
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) ->
        {
//            if (blockEntity instanceof FluidPipeBlockEntity<?> be)
//            {
//                InitialTicks.getInstance(world).queue(w ->
//                {
//                    if (!be.replaced)
//                    {
//                        be.onLoad(world);
//                    }
//                    else
//                    {
//                        be.replaced = false;
//                    }
//                });
//            }
        });
    }

    public void tick()
    {
        vertex.preTick();
        vertex.tick();
    }
}
