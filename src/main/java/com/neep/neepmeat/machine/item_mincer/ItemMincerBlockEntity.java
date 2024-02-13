package com.neep.neepmeat.machine.item_mincer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.MeatFluidUtil;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.FoodComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class ItemMincerBlockEntity extends SyncableBlockEntity implements MotorisedBlock, MotorisedBlock.DiagnosticsProvider
{
    protected float power;
    protected State state = State.IDLE;
    protected float processEnergy;

    protected ItemMincerStorage storage;

    protected final FluidPump pump = new FluidPump()
    {
        @Override
        public float getFlow()
        {
            return -0.5f;
        }

        @Override
        public AcceptorModes getMode()
        {
            return AcceptorModes.PUSH;
        }

        @Override
        public boolean isStorage()
        {
            return true;
        }
    };

    public ItemMincerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new ItemMincerStorage(this);
    }

    public ItemMincerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ITEM_MINCER, pos, state);
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            switch (state)
            {
                case IDLE ->
                {
                    if (canStart())
                    {
                        state = State.PROCESSING;
                        processEnergy = 15;
                        sync();
                    }
                }
                case PROCESSING ->
                {
                    processEnergy = Math.max(0, processEnergy - power);
                    if (processEnergy <= 0)
                    {
                        produceOutput(transaction);
                        state = State.IDLE;
                    }
                }
            }
            transaction.commit();
        }
        return true;
    }

    protected boolean canStart()
    {
        if (!storage.inputStorage.isEmpty())
        {
            FoodComponent food = storage.inputStorage.getResource().getObject().getFoodComponent();
            return food != null;
        }
        return false;
    }

    protected void produceOutput(TransactionContext context)
    {
        if (canStart())
        {
            try (Transaction inner = context.openNested())
            {
                FoodComponent food = storage.inputStorage.getResource().getObject().getFoodComponent();
                FluidVariant outputVariant = MeatFluidUtil.getVariant(food);
                storage.outputStorage.insert(outputVariant, FluidConstants.INGOT, inner);
                storage.inputStorage.extract(storage.inputStorage.getResource(), 1, inner);
                inner.commit();
            }
        }
    }

    @Override
    public void setInputPower(float power)
    {
        this.power = power;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("power", power);
        nbt.putInt("state", state.ordinal());
        nbt.putFloat("energy", processEnergy);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.power = nbt.getFloat("power");
        this.state = State.values()[nbt.getInt("state")];
        this.processEnergy = nbt.getFloat("energy");
        storage.readNbt(nbt);
    }

    public WritableStackStorage getInputStorage(Direction direction)
    {
        return direction == Direction.UP ? storage.inputStorage : null;
    }

    public WritableSingleFluidStorage getOutputStorage(Direction direction)
    {
        Direction facing = getCachedState().get(ItemMincerBlock.FACING);
        return facing == direction ? storage.outputStorage : null;
    }

    public FluidPump getPump(Direction direction)
    {
        Direction facing = getCachedState().get(ItemMincerBlock.FACING);
        return facing == direction ? pump : null;
    }

    @Override
    public Diagnostics getDiagnostics()
    {
        return Diagnostics.insufficientPower(false, power, 0);
    }

    public enum State
    {
        IDLE,
        PROCESSING
    }
}