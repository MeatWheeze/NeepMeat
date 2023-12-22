package com.neep.neepmeat.fluid_util.node;

import com.neep.neepmeat.fluid_util.AcceptorModes;
import com.neep.neepmeat.fluid_util.NMFluidNetwork;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/*
    An interface for fluid networks associated with a FluidNodeProvider's face.
 */
@SuppressWarnings("UnstableApiUsage")
public class FluidNode
{
    private final Direction face;
    private final BlockPos pos;
    public float flow;
    public AcceptorModes mode;
    private NMFluidNetwork network = null;
    public Map<FluidNode, Integer> distances = new HashMap<>();
    private final Storage<FluidVariant> storage;

    public FluidNode(BlockPos pos, Direction face, Storage<FluidVariant> storage, AcceptorModes mode, float flowMultiplier)
    {
        this.face = face;
        this.pos = pos;
        this.storage = storage;
        this.mode = mode;
        this.flow = mode.getFlow() * flowMultiplier;
    }

    @Override
    public String toString()
    {
        return "\n" + this.pos.toString() + " " + face;
    }

    public void setMode(AcceptorModes mode)
    {
        this.mode = mode;
        this.flow = mode.getFlow();
    }

    public void setNetwork(NMFluidNetwork network)
    {
//        System.out.println("called set network to replace " + this.network);
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

    public void rebuildNetwork(World world)
    {
//        if (network == null)
        {
//            network = new NMFluidNetwork(world, pos, face);
        }
//        network.rebuild(pos, face);
    }

    public Direction getFace()
    {
        return face;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public float getFlow()
    {
        return flow;
    }

    public void transmitFluid(FluidNode node)
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
            amountMoved = StorageUtil.move(storage, node.storage, variant -> true, (long) branchFlow, null);
//            System.out.println(amountMoved);
        }
        else
        {
            amountMoved = StorageUtil.move(node.storage, storage, variant -> true, (long) - branchFlow, null);

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
