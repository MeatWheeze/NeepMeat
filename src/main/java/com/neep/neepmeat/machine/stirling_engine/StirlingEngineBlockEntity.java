package com.neep.neepmeat.machine.stirling_engine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class StirlingEngineBlockEntity extends SyncableBlockEntity
{
    protected StirlingEngineStorage storage;

    public float speed = 0;
    public float angle;

    protected int burnTime;
//    protected int fuelTime;

    public StirlingEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new StirlingEngineStorage(this);
    }

    public StirlingEngineBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.STIRLING_ENGINE, pos, state);
    }

    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putInt("burn_time", burnTime);
//        nbt.putInt("fuel_time", fuelTime);
    }

    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.burnTime = nbt.getInt("burn_time");
//        this.fuelTime = nbt.getInt("fuel_time");
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        this.speed = nbt.getFloat("speed");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        nbt.putFloat("speed", speed);
        return nbt;
    }

    public void tick()
    {
        this.burnTime = Math.max(0, burnTime - 1);

        if (isBurning())
        {
            speed += 0.001;
            sync();
        }

        if (burnTime == 0)
        {
//            this.speed = 0;
            try (Transaction transaction = Transaction.openOuter())
            {
                int time = 0;
                if ((time = storage.decrementFuel(transaction)) > 0)
                {
                    this.burnTime = time;
                    transaction.commit();
                }
                else transaction.abort();
            }
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState blockState, StirlingEngineBlockEntity be)
    {
        be.tick();
    }

    public boolean isBurning()
    {
        return burnTime > 0;
    }

    public StirlingEngineStorage getStorage()
    {
        return storage;
    }
}