package com.neep.neepmeat.blockentity;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.machine.ConverterBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
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
import org.lwjgl.system.CallbackI;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class LargeConverterBlockEntity extends BlockEntity
{

    protected boolean isAssembled;
    protected int cooldown;

    public LargeConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public LargeConverterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.CONVERTER, pos, state);
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

    public void tick()
    {
        --cooldown;
        if (cooldown > 0)
            return;

        cooldown = 2;

        Transaction transaction = Transaction.openOuter();
        convert(FluidConstants.BUCKET / 64, transaction);
        transaction.commit();
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        this.cooldown = nbt.getInt("cooldown");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        System.out.println(getCachedState());
        super.writeNbt(nbt);

        nbt.putInt("cooldown", cooldown);

        return nbt;
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

    public static <E extends BlockEntity> void serverTick(World world, BlockPos pos, BlockState state, LargeConverterBlockEntity be)
    {
        be.tick();
    }
}
