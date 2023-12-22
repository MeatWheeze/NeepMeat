package com.neep.neepmeat.machine.homogeniser;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class HomogeniserBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
//    protected static final long PROCESS_AMOUNT = FluidConstants.BOTTLE;

    protected HomogeniserStorage storage = new HomogeniserStorage(this);
    protected State state = State.IDLE;

    protected final float maxProgress = 20;
    protected float progress = 0;
    protected float power = 0;

    public HomogeniserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putInt("state", state.ordinal());
        nbt.putFloat("progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.state = State.values()[nbt.getInt("state")];
        this.progress = nbt.getFloat("progress");
    }

    public WritableStackStorage getItemStorage(@Nullable Direction direction)
    {
        return storage.itemStorage;
    }

    public HomogeniserStorage getStorage()
    {
        return storage;
    }

    protected boolean canProcess(TransactionContext transaction)
    {
        var inputItemStorage = storage.itemStorage;

        if (!inputItemStorage.isEmpty())
        {
            var entry = MeatAdditives.getInstance().get(inputItemStorage.getResource().getItem());
            if (entry != null)
            {
                try (Transaction test = transaction.openNested())
                {
                    Storage<FluidVariant> inputFluidStorage = storage.getInputFluidStorage();

                    ResourceAmount<FluidVariant> inputVariant =
                            StorageUtil.findExtractableContent(inputFluidStorage, v -> v.isOf(NMFluids.STILL_C_MEAT), test);

                    if (inputVariant != null
                            && entry.canApply(inputVariant.resource())
                            && inputVariant.amount() >= entry.getAmount())
                    {

                        test.abort();
                        return true;
                    }

                    test.abort();
                }
            }
        }
        return false;
    }

    protected void ejectOutput(TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            var inputItemStorage = storage.itemStorage;
            if (!inputItemStorage.isEmpty())
            {
                var entry = MeatAdditives.getInstance().get(inputItemStorage.getResource().getItem());
                if (entry != null)
                {
                    Storage<FluidVariant> inputFluidStorage = storage.getInputFluidStorage();
                    Storage<FluidVariant> outputFluidStorage = storage.getOutputStorage();

                    ResourceAmount<FluidVariant> inputVariant =
                            StorageUtil.findExtractableContent(inputFluidStorage, v -> v.isOf(NMFluids.STILL_C_MEAT), inner);

                    if (inputVariant != null
                            && entry.canApply(inputVariant.resource())
                            && inputVariant.amount() >= entry.getAmount())
                    {

                        long extracted = inputFluidStorage.extract(inputVariant.resource(), entry.getAmount(), inner);

                        FluidVariant outputVariant = entry.apply(inputVariant.resource());

                        long inserted = outputFluidStorage.insert(outputVariant, extracted, inner);

                        if (inserted == extracted)
                        {
                            inner.commit();
                            return;
                        }
                    }

                    inner.abort();
                }
            }
        }
    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            switch (state)
            {
                case IDLE ->
                {
                    if (world.getTime() % 2 == 0 && canProcess(transaction))
                    {
                        progress = 0;

                        state = State.PROCESSING;
                        sync();
                    }
                }

                case PROCESSING ->
                {
                    progress = Math.min(maxProgress, progress + power);

                    if (progress == maxProgress)
                    {
                        progress = 0;

                        ejectOutput(transaction);
                        state = State.IDLE;
                        sync();
                    }
                }
            }
            transaction.commit();
        }
        return true;
    }

    float angle = 0;

    public void clientTick()
    {
        angle += 1.1;

        if (state == State.PROCESSING && !storage.itemStorage.isEmpty())
        {
            double radius = 0.2;

            double cx = getPos().getX() + 0.5;
            double cy = getPos().getY() + 1;
            double cz = getPos().getZ() + 0.5;

            double px = cx + Math.sin(angle) * radius;
            double pz = cz + Math.cos(angle) * radius;

            double vx = px - cx;
            double vy = 0.35;
            double vz = pz - cz;

            world.addParticle(
                    new ItemStackParticleEffect(ParticleTypes.ITEM, storage.itemStorage.getAsStack()),
                    px, cy, pz,
                    vx, vy, vz
            );
        }
    }

    @Override
    public void setInputPower(float power)
    {
        this.power = power;
    }

    @Override
    public float getLoadTorque()
    {
        return MotorisedBlock.super.getLoadTorque();
    }


    protected enum State
    {
        IDLE,
        PROCESSING
    }
}
