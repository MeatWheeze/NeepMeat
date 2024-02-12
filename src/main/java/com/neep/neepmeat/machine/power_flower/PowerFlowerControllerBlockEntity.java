package com.neep.neepmeat.machine.power_flower;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Queue;
import java.util.Set;

public class PowerFlowerControllerBlockEntity extends SyncableBlockEntity
{
    public static final FluidVariant INPUT_VARIANT = FluidVariant.of(Fluids.WATER);

    private int panels;

    private final FluidPump fluidPump = FluidPump.of(-0.5f, () -> AcceptorModes.PUSH, true);

    private final WritableSingleFluidStorage waterStorage = new WritableSingleFluidStorage(4 * FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return variant.isOf(Fluids.WATER);
        }
    };

    private final WritableSingleFluidStorage outputStorage = new WritableSingleFluidStorage(4 * FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return variant.isOf(NMFluids.STILL_ETHEREAL_FUEL);
        }
    };

    public PowerFlowerControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        if (world.getTime() % 80 == 0)
        {
            int newPanels = search(pos);
            if (newPanels != panels)
            {
                this.panels = newPanels;
            }
            updateState();
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            // Calculate amount of fluid to be produced from current power output
            long produceAmount = PowerUtils.absToAmount(NMFluids.STILL_ETHEREAL_FUEL, 20);

            // Extract from water tank
            long extracted = waterStorage.extract(INPUT_VARIANT, produceAmount, transaction);

            // Insert output fluid into internal buffer
            long inserted = outputStorage.insert(FluidVariant.of(NMFluids.STILL_ETHEREAL_FUEL), Math.min(produceAmount, extracted), transaction);

            if (inserted == produceAmount)
            {
                transaction.commit();
            }
            else
            {
                transaction.abort();
            }
        }
    }

    protected void updateState()
    {
        world.setBlockState(pos, NMBlocks.POWER_FLOWER_CONTROLLER.getDefaultState().with(PowerFlowerControllerBlock.VALID, panels > 0));
    }

    private int search(BlockPos start)
    {
        int panels = 0;

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
                    if (nextState.getBlock() instanceof PowerFlowerGrowthBlock)
                    {
                        queue.add(mutable.toImmutable());
                        visited.add(mutable.toImmutable());
                        panels++;

                        if (panels >= 40)
                        {
                            return panels;
                        }
                    }
                    else if (nextState.isOf(NMBlocks.POWER_FLOWER_CONTROLLER))
                    {
                        return 0;
                    }
                    else if (world.getBlockEntity(mutable) instanceof PowerFlowerFluidPortBlock.PFPortBlockEntity port)
                    {
                        port.setController(pos);
                    }
                }
            }
        }
        return panels;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("panels", panels);
        nbt.put("water_storage", waterStorage.toNbt(new NbtCompound()));
        nbt.put("output_storage", outputStorage.toNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.panels = nbt.getInt("panels");
        waterStorage.readNbt(nbt.getCompound("water_storage"));
        outputStorage.readNbt(nbt.getCompound("output_storage"));
    }

    public Storage<FluidVariant> getFluidStorage()
    {
        return waterStorage;
    }

    public FluidPump getFluidPump(Direction direction)
    {
        return fluidPump;
    }

    public Storage<FluidVariant> getOutputStorage(Direction direction)
    {
        return outputStorage;
    }
}
