package com.neep.neepmeat.machine.large_crusher;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;

public class LargeCrusherBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    private final LargeCrusherStorage storage = new LargeCrusherStorage(this);
    private float inputPower = 0;

    public LargeCrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putFloat("input_power", inputPower);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.inputPower = nbt.getFloat("input_power");
    }

    public void serverTick(ServerWorld world)
    {
        if (world.getTime() % 4 == 0)
        {
            Direction facing = getCachedState().get(LargeCrusherBlock.FACING);
            Direction clockwise = facing.rotateYClockwise();
            Direction aclockwise = facing.rotateYCounterclockwise();
            Box box = new Box(pos.up(2))
                .stretch(facing.getOffsetX(), facing.getOffsetY(), facing.getOffsetZ())
                .stretch(clockwise.getOffsetX(), clockwise.getOffsetY(), clockwise.getOffsetZ())
                .stretch(aclockwise.getOffsetX(), aclockwise.getOffsetY(), aclockwise.getOffsetZ())
                    ;

            List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, box, e -> true);
            if (!items.isEmpty())
            {
                ItemEntity item = items.get(0);
                try (Transaction transaction = Transaction.openOuter())
                {
                    ItemVariant variant = ItemVariant.of(item.getStack());
                    long inserted = storage.inputStorage.insert(variant, item.getStack().getCount(), transaction);
                    item.getStack().decrement((int) inserted);
                    transaction.commit();
                }
            }
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            for (var view : storage.outputStorage)
            {
                if (!view.isResourceBlank())
                {
                    int extracted = ItemPipeUtil.stackToAny(world, pos, Direction.DOWN, view.getResource(), view.getAmount(), transaction);
                    view.extract(view.getResource(), extracted, transaction);
                }
            }
            transaction.commit();
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            int occupied = 0;
            for (var slot : storage.slots)
            {
                if (slot.getRecipe() != null)
                    occupied++;
            }

            // Divide the progress increment evenly across occupied slots.
            float progressIncrement = inputPower * 4 / occupied;
            for (var slot : storage.slots)
            {
                slot.tick(progressIncrement, transaction);
            }
            transaction.commit();
        }
    }

    @Override
    public void sync()
    {
        super.sync();
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
        return false;
    }

    @Override
    public void setInputPower(float power)
    {
        inputPower = power;
    }

    public Storage<ItemVariant> getInputStorage(Direction unused)
    {
        return storage.inputStorage;
    }

    public List<LargeCrusherStorage.InputSlot> getSlots()
    {
        return storage.slots;
    }

    public float getProgressIncrement()
    {
        return inputPower;
    }
}
