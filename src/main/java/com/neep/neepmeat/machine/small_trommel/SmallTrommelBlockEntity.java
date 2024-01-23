package com.neep.neepmeat.machine.small_trommel;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.block.machine.TrommelBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.recipe.TrommelRecipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class SmallTrommelBlockEntity extends SyncableBlockEntity implements MotorisedBlock, MotorisedBlock.DiagnosticsProvider
{
    public static final float INCREMENT_MIN = 0.1f;
    public static final float INCREMENT_MAX = 2;
    public static long CONVERT_MIN = 100;
    public static long BASE_AMOUNT = 9000;

    public TrommelStorage storage;
    public FluidVariant currentFluid;

    protected int totalProgress;
    protected float progress;
    public float renderProgress;
    private float progressIncrement;
    protected Random random;
    private float power = 0;
    private float minPower = 0.02f;

    public SmallTrommelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storage = new TrommelStorage(this);
        this.random = new Random(pos.asLong());
    }

    public SmallTrommelBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.SMALL_TROMMEL, pos, state);
    }

    public Storage<FluidVariant> getInputStorage(Direction direction)
    {
        return direction == Direction.UP ? storage.fluidInput : null;
    }

    public Storage<FluidVariant> getOutputFluidStorage()
    {
        return storage.fluidOutput;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);

        storage.writeNbt(nbt);

        nbt.putFloat("progress", progress);
        nbt.putFloat("progressIncrement", progressIncrement);
        nbt.putInt("totalProgress", totalProgress);

        if (currentFluid != null)
            nbt.put("currentFluid", currentFluid.toNbt());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        storage.readNbt(nbt);

        this.progress = nbt.getFloat("progress");
        this.progressIncrement = nbt.getFloat("progressIncrement");
        this.totalProgress = nbt.getInt("totalProgress");
        this.currentFluid = FluidVariant.fromNbt((NbtCompound) nbt.get("currentFluid"));
    }

    public void convert()
    {
        FluidVariant inputVariant = storage.input().getResource();

        TrommelRecipe recipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.TROMMEL, storage).orElse(null);
        if (recipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (recipe.takeInputs(storage, transaction) && recipe.ejectOutputs(storage, transaction))
                {
                    transaction.commit();
                }
                else transaction.abort();
            }
            return;
        }

        OreFatRegistry.Entry entry = OreFatRegistry.getFromVariant(inputVariant);
        if (inputVariant.isOf(NMFluids.STILL_DIRTY_ORE_FAT) && entry != null)
        {
            long inputAmount = (long) Math.floor(BASE_AMOUNT);
            long extractable = MeatlibStorageUtil.simulateExtract(storage.fluidInput, inputVariant, inputAmount, null);
            boolean produceExtra = random.nextFloat() > 0.5;
            long outputAmount = produceExtra ? extractable : extractable + extractable;

            if (outputAmount < CONVERT_MIN)
                return;

            try (Transaction transaction = Transaction.openOuter())
            {

                NbtCompound nbt = inputVariant.copyNbt();
                long extracted = storage.fluidInput.extract(inputVariant, inputAmount, transaction);
                long inserted = storage.fluidOutput.insert(FluidVariant.of(NMFluids.STILL_CLEAN_ORE_FAT, nbt), outputAmount, transaction);
                if (extracted > 0 && inserted == outputAmount)
                {
                    transaction.commit();
                    currentFluid = storage.fluidInput.getResource();
                }
                else
                {
                    currentFluid = FluidVariant.blank();
                    transaction.abort();
                }
            }
        }
    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        Direction facing = getCachedState().get(TrommelBlock.FACING);
        try (Transaction transaction = Transaction.openOuter())
        {
            ItemPipeUtil.storageToAny((ServerWorld) world, storage.itemOutput(), pos.offset(facing), facing, transaction);
            transaction.commit();
        }

        totalProgress = 30;
        currentFluid = !storage.fluidInput.getResource().isBlank() ? storage.fluidInput.getResource() : null;
        if (currentFluid != null && progressIncrement != INCREMENT_MIN)
        {
            progress = Math.min(totalProgress, progress + progressIncrement);
        }
        else progress = 0;
        if (progress >= totalProgress)
        {
            progress = 0;
            convert();
        }
        softSync();
        return false;
    }

    @Override
    public void setInputPower(float power)
    {
        this.power = power;
        if (power >= minPower)
        {
            progressIncrement = MathHelper.lerp(power, INCREMENT_MIN, 2);
        }
        else
        {
            progressIncrement = 0;
        }
    }

    public Storage<ItemVariant> getOutputItemStorage()
    {
        return storage.itemOutput;
    }

    @Override
    public Diagnostics get()
    {
        return Diagnostics.insufficientPower(power < minPower, power, minPower);
    }

    public static class Structure extends BlockEntity
    {
        public Structure(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public Structure(BlockPos pos, BlockState state)
        {
            super(NMBlockEntities.SMALL_TROMMEL_STRUCTURE, pos, state);
        }
    }
}
