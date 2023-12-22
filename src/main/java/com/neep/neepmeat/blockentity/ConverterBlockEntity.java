package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class ConverterBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{

    protected int cooldown;

    public boolean stage;
    public boolean running;
    protected long conversionTime;
    protected short baseAmount = (short) (FluidConstants.BUCKET / 300 / 2);
    protected float multiplier = 1;

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

    public void checkBurnerBlock()
    {
        Direction facing = getCachedState().get(HorizontalFacingBlock.FACING).getOpposite();
        BlockPos burnerPos = pos.offset(facing);
        BlockState burnerState = world.getBlockState(burnerPos);
        if (burnerState.isOf(Blocks.FURNACE))
        {
            FurnaceAccessor furnace = (FurnaceAccessor) world.getBlockEntity(burnerPos);
            if (furnace.getBurnTime() == 0)
            {
                ItemStack itemStack = furnace.getInventory().get(1);
                int time = furnace.callGetFuelTime(itemStack);
                furnace.setFuelTime(time);
                furnace.setBurnTime(time);
                itemStack.decrement(1);
                world.setBlockState(burnerPos, burnerState.with(FurnaceBlock.LIT, false));
            }
            else
            {
                world.setBlockState(burnerPos, burnerState.with(FurnaceBlock.LIT, true));
            }
            furnace.setCookTime(0);
            this.conversionTime = furnace.getBurnTime();
            this.multiplier = 2;
        }
        else if (burnerState.isOf(Blocks.FIRE))
        {
            this.conversionTime = 1;
            this.multiplier = 1;
        }
        else if (burnerState.isOf(Blocks.LAVA) || burnerState.isOf(Blocks.LAVA_CAULDRON))
        {
            this.conversionTime = 1;
            this.multiplier = 1.5f;
        }
        else
        {
            this.conversionTime = 0;
            this.multiplier = 1;
        }
    }

    public void tick()
    {
        --cooldown;
//        if (cooldown > 0)
//            return;

//        cooldown = 2;

        checkBurnerBlock();
        if (conversionTime > 0)
        {
            Transaction transaction = Transaction.openOuter();
            long convertAmount = (long) (baseAmount * multiplier);
            this.running = convert(convertAmount, transaction) == convertAmount;
            transaction.commit();
        }
        this.running = running && conversionTime > 0;
        this.sync();
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        this.cooldown = nbt.getInt("cooldown");
//        this.stage = nbt.getShort("stage");
//        this.inAmount = nbt.getLong("in_amount");
//        this.outAmount = nbt.getLong("out_amount");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);

        nbt.putInt("cooldown", cooldown);
//        nbt.putShort("stage", stage);
//        nbt.putLong("in_amount", inAmount);
//        nbt.putLong("out_amount", outAmount);

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

    public static <E extends BlockEntity> void serverTick(World world, BlockPos pos, BlockState state, ConverterBlockEntity be)
    {
        be.tick();
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
