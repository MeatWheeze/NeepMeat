package com.neep.neepmeat.machine.transducer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class TransducerBlockEntity extends SyncableBlockEntity
{
    protected int baseAmount = (short) (FluidConstants.BUCKET / 150);

    protected WritableSingleFluidStorage storage = new WritableSingleFluidStorage(FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        public boolean supportsInsertion()
        {
            return false;
        }
    };

    private int conversionTime;
    private float multiplier;
    public boolean running;

    public TransducerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TransducerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TRANSDUCER, pos, state);
    }

    public SingleVariantStorage<FluidVariant> getStorage(Direction direction)
    {
        return storage;
    }

    public FluidPump getPump(Direction direction)
    {
        return FluidPump.of(0.5f, true);
    }

    public void checkBurnerBlock()
    {
        BlockPos burnerPos = pos.down();
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
            this.multiplier = 2f;
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
        if (conversionTime > 0)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                long convertAmount = (long) (baseAmount * multiplier);
                long inserted = storage.insert(FluidVariant.of(NMFluids.STILL_ETHEREAL_FUEL), convertAmount, transaction);

                if (inserted == convertAmount)
                {
                    this.running = true;
                    transaction.commit();
                }
                else
                {
                    this.running = false;
                    transaction.abort();
                }
            }
        }
        this.running = running && conversionTime > 0;
        checkBurnerBlock();
        this.sync();
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, TransducerBlockEntity be)
    {
        be.tick();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putInt("conversionTime", conversionTime);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.conversionTime = nbt.getInt("conversionTime");
    }
}
