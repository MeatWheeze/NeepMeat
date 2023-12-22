package com.neep.neepmeat.fluid_util.node;

import com.neep.neepmeat.fluid_util.AcceptorModes;
import com.neep.neepmeat.fluid_util.FluidNetwork;
import com.neep.neepmeat.fluid_util.NMFluidNetwork;
import com.sun.jna.platform.win32.WinUser;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/*
    An interface for fluid networks associated with a
 */
@SuppressWarnings("UnstableApiUsage")
public class FluidNode
{
    private final Direction face;
    private final BlockPos pos;
    private final NodePos nodePos;
    public float flow;
    public float flowMultiplier;
    public AcceptorModes mode;
    private NMFluidNetwork network = null;
    public long networkId;
    public Map<FluidNode, Integer> distances = new HashMap<>();
    private Storage<FluidVariant> storage;

    public boolean needsDeferredLoading;

    public FluidNode(BlockPos pos, Direction face, Storage<FluidVariant> storage, AcceptorModes mode, float flowMultiplier)
    {
        this.face = face;
        this.pos = pos;
        this.nodePos = new NodePos(pos, face);
        this.storage = storage;
        this.mode = mode;
        this.flowMultiplier = flowMultiplier;
        this.flow = mode.getFlow() * flowMultiplier;
    }

    // From NBT
    public FluidNode(BlockPos pos, Direction face, AcceptorModes mode, float flowMultiplier, long networkId)
    {
        this.face = face;
        this.pos = pos;
        this.nodePos = new NodePos(pos, face);
        this.mode = mode;
        this.flowMultiplier = flowMultiplier;
        this.flow = mode.getFlow() * flowMultiplier;
        this.networkId = networkId;
        this.storage = null;
        this.needsDeferredLoading = true;

        FluidNetwork.QUEUED_NODES.add(this);

    }

    @Override
    public String toString()
    {
        return "\n" + this.pos.toString() + " " + face + " storage: " + storage;
    }

    public static FluidNode fromNbt(NbtCompound nbt)
    {
        BlockPos pos = BlockPos.fromLong(nbt.getLong("position"));
        Direction face = Direction.byId(nbt.getInt("direction"));
        AcceptorModes mode = AcceptorModes.byId(nbt.getInt("mode"));
        long networkId = nbt.getLong("network_id");
        float flowMultiplier = nbt.getFloat("multiplier");

       FluidNode node = new FluidNode(pos, face, mode, flowMultiplier, networkId);
//       node.loadDeferred(world);
       return node;
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("direction", face.getId());
        nbt.putLong("position", pos.asLong());
        nbt.putInt("mode", mode.getId());
        nbt.putLong("network_id", networkId);
        nbt.putFloat("multiplier", flowMultiplier);
        return nbt;
    }

    public void loadDeferred(ServerWorld world)
    {
        if (!(needsDeferredLoading && storage == null) || !world.getServer().isOnThread())
        {
            return;
        }
        load(world);
        Optional<NMFluidNetwork> net = NMFluidNetwork.tryCreateNetwork(world, pos, Direction.NORTH);
    }

    private void load(ServerWorld world)
    {
        if (!(needsDeferredLoading && storage == null) || !world.getServer().isOnThread())
        {
            return;
        }
        findStorage(world);
    }

    public void findStorage(ServerWorld world)
    {
        Storage<FluidVariant> storage;
        if ((storage = FluidStorage.SIDED.find(world, pos.offset(face), face.getOpposite())) != null)
        {
            this.storage = storage;
            this.needsDeferredLoading = false;
        }
    }

    public void setMode(AcceptorModes mode)
    {
        this.mode = mode;
        this.flow = mode.getFlow();
    }

    public void setNetwork(ServerWorld world, NMFluidNetwork network)
    {
        load(world);
        setNetwork(network);
    }

    public void setNetwork(NMFluidNetwork network)
    {
        if (!(this.network == null) && !this.network.equals(network))
        {
            this.network.removeNode(new NodePos(pos, face));
        }
        if (this.network != null)
        {
            System.out.println(network.uid + " replaces " + this.network.uid);
        }
        this.network = network;
        distances.clear();
    }

    public NMFluidNetwork getNetwork()
    {
        return network;
    }

    // Removed node from and revalidates the network
    public void onRemove()
    {
        System.out.println("removed " + this);
        if (!(this.network == null))
        {
            network.removeNode(new NodePos(pos, face));
            network = null;
        }
        distances.clear();
    }

    public void tick(World world)
    {
//        if (network != null)
//        {
//            network.tick();
//        }
    }

    public Direction getFace()
    {
        return face;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public NodePos getNodePos()
    {
        return this.nodePos;
    }

    public float getFlow()
    {
        return flow;
    }

    public Storage<FluidVariant> getStorage(ServerWorld world)
    {
        if (!world.getServer().isOnThread())
        {
            return null;
        }
        if (storage != null)
        {
            return storage;
        }
        else
        {
//            loadDeferred(world);
        }
        return null;
    }

    public void transmitFluid(ServerWorld world, FluidNode node)
    {
        if (distances.get(node) == null
                || node.mode == AcceptorModes.NONE
                || this.mode == AcceptorModes.NONE)
        {
//            System.out.println("transmit null");
            return;
        }

        // Here is the most realistic flow rate calculation that you have ever seen.
        // Laminar Poiseuille flow: Q = (R^4 pi) / (8 * µ) * dP/dX.

        // Geometrical solution for velocity in branched pipes:
        // https://physics.stackexchange.com/questions/31852/flow-of-liquid-among-branches
        // Useful equation:

        float r = 0.5f;

        // This is supposed to calculate sum(r^4 / L)
        AtomicReference<Float> sum = new AtomicReference<>((float) 0);
        distances.values().forEach((distance) -> sum.updateAndGet(v -> (v + ((float) Math.pow(r, 4) / (float) distance))));
//
        float branchFlow = 500 * (flow) * (float) ((Math.pow(r, 4) / (distances.get(node))) / sum.get());
//        System.out.println(branchFlow);

//        float pressureGradient = (node.getPressure() - getPressure()) / distances.get(node);
//        float flow = - 4050 * pressureGradient / distances.values().size();

//        long transferAmount = (branchFlow) > 0 ? (long) branchFlow : 0;
//        long transferAmount = (flow) > 0 ? (long) flow : 0;
        long amountMoved = 0;
        if (branchFlow >= 0)
        {
            amountMoved = StorageUtil.move(getStorage(world), node.getStorage(world), variant -> true, (long) branchFlow, null);
//            System.out.println(amountMoved);
        }
        else
        {
            amountMoved = StorageUtil.move(node.getStorage(world), getStorage(world), variant -> true, (long) - branchFlow, null);

        }
//        System.out.print(node + ", " + node.getPressure() + ", amount: " + amountMoved);
    }

    public static void flow(Storage<FluidVariant> from, Storage<FluidVariant> to)
    {
//        try (Transaction outerTransaction = Transaction.openOuter())
//        {
//            StorageUtil.move(from, to, )
//             (A) some transaction operations
//             (C) even more operations
//            outerTransaction.commit(); // This is an outer transaction: changes (A), (B) and (C) are applied.
//        }
        // If we hadn't committed the outerTransaction, all changes (A), (B) and (C) would have been reverted.
    }

}
