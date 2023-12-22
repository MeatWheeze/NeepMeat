package com.neep.neepmeat.machine.converter;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.Burner;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("UnstableApiUsage")
public class ConverterBlockEntity extends SyncableBlockEntity
{

    protected int cooldown;

    public boolean stage;
    public boolean running;
    protected boolean needsUpdate = true;
    protected long outputPower;

    protected List<Burner> burners = new ArrayList<>(1);

    // Rendering only
    public float renderIn;
    public float renderOut;

    public ConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public ConverterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.CONVERTER, pos, state);
//        this(null, pos, state);
    }

    public Optional<Storage<FluidVariant>> getInputTank()
    {
        BlockPos pos = getPos().add(0, 1, 0);
        return Optional.ofNullable(FluidStorage.SIDED.find(world, pos, Direction.DOWN));
    }

    public Optional<Storage<FluidVariant>> getOutputTank()
    {
        BlockPos pos = getPos().offset(getCachedState().get(HorizontalFacingBlock.FACING).getOpposite()).add(0, 1, 0);
        return Optional.ofNullable(FluidStorage.SIDED.find(world, pos, Direction.DOWN));
    }

    protected void updateBurner()
    {
        burners.clear();
        BlockPos burnerPos = pos.offset(getCachedState().get(HorizontalFacingBlock.FACING).getOpposite());
        Burner burner = Burner.LOOKUP.find(world, burnerPos, null);
        if (burner != null) burners.add(burner);
    }

    public void tick()
    {
        if (needsUpdate)
        {
            updateBurner();
        }

        outputPower = 0;
        for (Burner burner : burners)
        {
            burner.tickPowerConsumption();
            outputPower += burner.getOutputPower();
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            // Calculate amount of fluid to be produced from current power output
            long produceAmount = PowerUtils.absToAmount(NMFluids.STILL_CHARGED_WORK_FLUID, outputPower);

            this.running = convert(produceAmount, transaction) == produceAmount;
            transaction.commit();
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.cooldown = nbt.getInt("cooldown");
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);

        nbt.putInt("cooldown", cooldown);
    }

    public long convert(long amount, Transaction transaction)
    {
        Transaction nested = transaction.openNested();
        AtomicLong extracted = new AtomicLong();
        AtomicLong inserted = new AtomicLong();
        getInputTank().ifPresent(tank -> extracted.set(tank.extract(NMFluids.UNCHARGED, amount, transaction)));
        getOutputTank().ifPresent(tank -> inserted.set(tank.insert(NMFluids.CHARGED, extracted.get(), transaction)));

        if (extracted.get() == amount && inserted.get() == amount)
        {
            nested.commit();
            return inserted.get();
        }
        nested.abort();
        return 0;
    }

    public static <E extends BlockEntity> void serverTick(World world, BlockPos pos, BlockState state, ConverterBlockEntity be)
    {
        be.tick();
    }

    public void update()
    {
        needsUpdate = true;
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        this.running = tag.getBoolean("running");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        tag.putBoolean("running", running);
        return tag;
    }
}
