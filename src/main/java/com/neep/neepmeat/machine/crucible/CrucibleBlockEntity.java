package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CrucibleBlockEntity extends SyncableBlockEntity
{
    protected CrucibleStorage storage;

    public CrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new CrucibleStorage(this);
    }

    public CrucibleBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.CRUCIBLE, pos, state);
    }

    public CrucibleStorage getStorage()
    {
        return storage;
    }

    public CombinedStorage<FluidVariant, SingleVariantStorage<FluidVariant>> getOutput()
    {
        List<SingleVariantStorage<FluidVariant>> storages = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            if (direction.getAxis().equals(Direction.Axis.Y))
                continue;

            BlockPos offset = pos.offset(direction);
            if (world.getBlockEntity(offset) instanceof AlembicBlockEntity be)
            {
                storages.add(be.getStorage(null));
            }
        }
        return new CombinedStorage<>(storages);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }

    public void receiveItem(ItemEntity entity)
    {
        ItemStack fuelStack = entity.getStack();
        Integer time;
        if ((time = FuelRegistry.INSTANCE.get(fuelStack.getItem())) != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                Storage<FluidVariant> alembic = getOutput();
                long maxAmount = (long) time * fuelStack.getCount();
                FluidVariant variant = FluidVariant.of(NMFluids.STILL_ETHEREAL_FUEL);

                long maxInserted = alembic.simulateInsert(variant, maxAmount, transaction);
                int maxCount = (int) maxInserted / time;

                long inserted = alembic.insert(FluidVariant.of(NMFluids.STILL_ETHEREAL_FUEL), (long) maxCount * time, transaction);
                fuelStack.decrement(maxCount);

                transaction.commit();
            }
        }
    }
}
