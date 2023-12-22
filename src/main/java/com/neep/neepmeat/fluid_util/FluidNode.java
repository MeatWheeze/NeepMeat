package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/*
    An interface for fluid networks associated with a FluidNodeProvider's face.
 */
@SuppressWarnings("UnstableApiUsage")
public class FluidNode
{
    private final Direction face;
    private final BlockPos pos;
    public float pressure;
    public FluidAcceptor.AcceptorModes mode;
    private NMFluidNetwork network = null;
    public Map<FluidNode, Integer> distances = new HashMap<>();
    private final Storage<FluidVariant> storage;

    public FluidNode(BlockPos pos, Direction face, Storage<FluidVariant> storage, FluidAcceptor.AcceptorModes mode, float pressure)
    {
        this.face = face;
        this.pos = pos;
        this.storage = storage;
        this.mode = mode;
        this.pressure = pressure;
    }

    @Override
    public String toString()
    {
        return "\n" + this.pos.toString() + " " + face;
    }

    public void setNetwork(NMFluidNetwork network)
    {
        this.network = network;
        distances.clear();
    }

    public void tick(World world)
    {
        if (network != null)
        {
            network.tick();
        }
    }

    public void rebuildNetwork(World world)
    {
        if (network == null)
        {
            network = new NMFluidNetwork(world);
        }
        network.rebuild(pos, face);
    }

    public Direction getFace()
    {
        return face;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public float getPressure()
    {
        return pressure;
    }

    public void transmitFluid(FluidNode node)
    {
        if (distances.get(node) == null)
        {
            System.out.println("transmit null");
            return;
        }

        // Here is the most realistic flow rate calculation that you have ever seen.
        // Laminar Poiseuille flow: Q = (R^4 pi) / (8 * µ) * dP/dX.

        // Geometrical solution for velocity in branched pipes:
        // https://physics.stackexchange.com/questions/31852/flow-of-liquid-among-branches
        // Useful equation:

        float r = 0.5f;

        // I don't know what this does.
        AtomicReference<Float> sum = new AtomicReference<>((float) 0);
        distances.values().forEach((distance) -> sum.updateAndGet(v -> (v + ((float) Math.pow(r, 4) / (float) distance))));
//
        float branchFlow = 500 * (pressure - node.getPressure()) * (float) ((Math.pow(r, 4) / distances.get(node)) / sum.get());

//        float pressureGradient = (node.getPressure() - getPressure()) / distances.get(node);
//        float flow = - 4050 * pressureGradient / distances.values().size();

        long transferAmount = (branchFlow) > 0 ? (long) branchFlow : 0;
//        long transferAmount = (flow) > 0 ? (long) flow : 0;
        long amountMoved = StorageUtil.move(storage, node.storage, variant -> true,  transferAmount, null);
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
