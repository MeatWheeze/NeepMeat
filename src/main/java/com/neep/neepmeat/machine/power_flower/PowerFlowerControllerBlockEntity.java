package com.neep.neepmeat.machine.power_flower;

import com.google.common.collect.Iterators;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.processing.MeatFluidUtil;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

public class PowerFlowerControllerBlockEntity extends SyncableBlockEntity
{
    public static final FluidVariant INPUT_VARIANT = FluidVariant.of(Fluids.WATER);

    private int topPanels;
    private int blocks;

    private final FluidPump fluidPump = FluidPump.of(-0.5f, () -> AcceptorModes.PUSH, true);

    private final WritableSingleFluidStorage waterStorage = new WritableSingleFluidStorage(4 * FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return variant.isOf(Fluids.WATER);
        }
    };

    private final WritableSingleFluidStorage foodStorage = new WritableSingleFluidStorage(4 * FluidConstants.BUCKET, this::markDirty);

    private final WritableSingleFluidStorage outputStorage = new WritableSingleFluidStorage(4 * FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return variant.isOf(NMFluids.STILL_ETHEREAL_FUEL);
        }
    };

    private final CombinedStorage storage = new CombinedStorage();

    public PowerFlowerControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    /**
     * @return A pair of energy per consumption and the amount to consume.
     */
    public static IntIntPair foodEnergy(FluidVariant fluid)
    {
        if (fluid.isOf(NMFluids.STILL_MEAT))
            return IntIntPair.of(3, 2);

        if (fluid.isOf(NMFluids.STILL_FEED))
            return IntIntPair.of(4, 2);

        if (fluid.isOf(NMFluids.STILL_C_MEAT))
        {
            return IntIntPair.of((int) Math.ceil(MeatFluidUtil.getHunger(fluid) / 2), 2);
        }

        return IntIntPair.of(0, 0);
    }

    public void serverTick()
    {
        if (world.getTime() % 80 == 0)
        {
            search(pos);
            updateState();
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            long outputAmount = 0;
            var food = foodEnergy(foodStorage.variant);
            int foodEnergy = food.firstInt();
            int consume = food.secondInt();
            if (foodEnergy != 0)
            {
                // The number of full blocks determines output power via food consumption rate
                int foodDroplets = consume * (blocks - topPanels);

                long foodExtracted = foodStorage.extract(foodStorage.variant, foodDroplets, transaction);

                outputAmount += PowerUtils.absToAmount(NMFluids.STILL_ETHEREAL_FUEL, foodExtracted * foodEnergy);
            }

            if (topPanels > 0)
            {
                // The number of top blocks determines output power directly
                int passivePower = 6 * topPanels;

                // Calculate amount of fluid to be produced from current power output
                long passiveAmount = PowerUtils.absToAmount(NMFluids.STILL_ETHEREAL_FUEL, passivePower);

                // Extract from water tank
                long extracted = waterStorage.extract(INPUT_VARIANT, passiveAmount, transaction);

                // Insert output fluid into internal buffer
                outputAmount += Math.min(passiveAmount, extracted);
            }

            long inserted = outputStorage.insert(FluidVariant.of(NMFluids.STILL_ETHEREAL_FUEL), outputAmount, transaction);

            if (inserted == outputAmount)
                transaction.commit();
            else
                transaction.abort();
        }
    }

    protected void updateState()
    {
        world.setBlockState(pos, NMBlocks.POWER_FLOWER_CONTROLLER.getDefaultState().with(PowerFlowerControllerBlock.VALID, blocks > 0));
    }

    private void search(BlockPos start)
    {
        blocks = 0;
        topPanels = 0;

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
                        if (nextState.get(PowerFlowerGrowthBlock.GROWTH) == 1)
                            topPanels++;

                        blocks++;

                        queue.add(mutable.toImmutable());
                        visited.add(mutable.toImmutable());

                        if (blocks >= 160)
                        {
                            return;
                        }
                    }
                    else if (nextState.isOf(NMBlocks.POWER_FLOWER_CONTROLLER))
                    {
                        return;
                    }
                    else if (world.getBlockEntity(mutable) instanceof PowerFlowerFluidPortBlock.PFPortBlockEntity port)
                    {
                        port.setController(pos);
                    }
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("blocks", blocks);
        nbt.putInt("panels", topPanels);
        nbt.put("water_storage", waterStorage.toNbt(new NbtCompound()));
        nbt.put("food_storage", foodStorage.toNbt(new NbtCompound()));
        nbt.put("output_storage", outputStorage.toNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.topPanels = nbt.getInt("panels");
        this.blocks = nbt.getInt("blocks");
        waterStorage.readNbt(nbt.getCompound("water_storage"));
        foodStorage.readNbt(nbt.getCompound("food_storage"));
        outputStorage.readNbt(nbt.getCompound("output_storage"));
    }

    public Storage<FluidVariant> getFluidStorage()
    {
        return storage;
    }

    public FluidPump getFluidPump(Direction direction)
    {
        return fluidPump;
    }

    public Storage<FluidVariant> getOutputStorage(Direction direction)
    {
        return outputStorage;
    }

    private class CombinedStorage implements Storage<FluidVariant>
    {
        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            if (resource.isOf(Fluids.WATER))
                return waterStorage.insert(resource, maxAmount, transaction);

            return foodStorage.insert(resource, maxAmount, transaction);
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator()
        {
            return Iterators.concat(waterStorage.iterator(), foodStorage.iterator());
        }
    }
}
