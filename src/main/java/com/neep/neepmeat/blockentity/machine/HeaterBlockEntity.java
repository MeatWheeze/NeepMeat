package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeaterBlockEntity extends BloodMachineBlockEntity<HeaterBlockEntity>
{
    protected FurnaceAccessor accessor;

    protected HeaterBlockEntity(BlockEntityType<HeaterBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public HeaterBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.HEATER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, HeaterBlockEntity blockEntity)
    {
        blockEntity.tick(state);
    }

    public boolean refreshCache(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos.offset(state.get(HeaterBlock.FACING))) instanceof FurnaceAccessor furnace)
        {
            accessor = furnace;
            return true;
        }
        else
        {
            accessor = null;
            return false;
        }
    }

    public void tick(BlockState state)
    {
        if (accessor == null)
        {
            if (!refreshCache(getWorld(), getPos(), getCachedState()))
            {
                return;
            }
        }

        Transaction transaction = Transaction.openOuter();
        long amount = FluidConstants.BUCKET / 300;
        if (doWork(amount, transaction) > 0)
        {
            accessor.setBurnTime(10);
        }
        transaction.commit();
//        if (outputBuffer.getCapacity() - outputBuffer.getAmount() >= transfer && inputBuffer.getAmount() >= transfer)
//        {
//            Transaction transaction = Transaction.openOuter();
//            long transferred = inputBuffer.extractDirect(NMFluids.CHARGED, transfer, transaction);
//            long inserted = outputBuffer.insertDirect(NMFluids.UNCHARGED, transferred, transaction);
//            if (transferred >= transfer)
//            {
//                accessor.setBurnTime(10);
//                updateBlockState(accessor, getWorld(), getPos().offset(getCachedState().get(HeaterBlock.FACING)));
//            }
//            transaction.commit();
//        }
    }

    public static void updateBlockState(FurnaceAccessor accessor, World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        state = state.with(AbstractFurnaceBlock.LIT, accessor.getBurnTime() > 0);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
    }
}
