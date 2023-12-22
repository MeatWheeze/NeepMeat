package com.neep.neepmeat.transport.machine.fluid;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;

public class FluidPipeBlockEntity extends BlockEntity
{
    public NbtCompound queuedNbt;
    protected PipeNetwork network;
    protected  BlockPipeVertex vertex = new BlockPipeVertex();

    public class BlockPipeVertex extends SimplePipeVertex
    {
        private final HashSet<NodeSupplier> nodes = new HashSet<>();

        @Override
        public void setNetwork(PipeNetwork network)
        {
            super.setNetwork(network);
            FluidPipeBlockEntity.this.network = network;
        }

        @Override
        public boolean canSimplify()
        {
            return super.canSimplify() && nodes.isEmpty();
        }

        public void updateNodes(ServerWorld world, BlockPos pos, BlockState state)
        {
            nodes.clear();
            FluidTransport.findFluidPipe(world, pos, state).ifPresent(p ->
            {
                for (Direction direction : p.getConnections(state, d -> true))
                {
                    NodeSupplier node = FluidNodeManager.getInstance(world).getNodeSupplier(new NodePos(pos, direction));
                    if (node.get() != null)
                    {
                        nodes.add(node);
                    }
                }
            });

        }

        @Override
        public String toString()
        {
            StringBuilder adj = new StringBuilder();
            for (PipeVertex v : getAdjVertices())
            {
                if (v != null) adj.append(System.identityHashCode(v)).append(", ");
            }
            return "Vertex@"+System.identityHashCode(this)+"{connection=" + adj + "nodes: " + nodes + ", head:" + getTotalHead() + "}";
        }
    };

    public FluidPipeBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.NODE_BLOCK_ENTITY, pos, state);
    }

    public FluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
        if (getWorld() instanceof ServerWorld serverWorld && world.getServer().isOnThread() && queuedNbt != null)
        {
            FluidNodeManager.getInstance(getWorld()).readNodes(getPos(), queuedNbt, serverWorld);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        queuedNbt = nbt.copy();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt = FluidNodeManager.getInstance(getWorld()).writeNodes(getPos(), nbt);
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();

        // Accessing FluidNodeManager on client must fail
        if (world.isClient()) return;

        if (!world.isPosLoaded(pos.getX(), pos.getY()))
        {
            FluidNodeManager.getInstance(world).entityUnloaded(pos);
        }
        else
        {
            FluidNodeManager.getInstance(world).entityRemoved(pos);
        }
    }

    public void update(PipeNetwork.UpdateReason reason)
    {
        if (network != null)
        {
            network.update(pos, vertex, reason);
        }
    }

    public boolean isCreatedDynamically()
    {
        return false;
    }

    public BlockPipeVertex getPipeVertex()
    {
        return vertex;
    }
}
