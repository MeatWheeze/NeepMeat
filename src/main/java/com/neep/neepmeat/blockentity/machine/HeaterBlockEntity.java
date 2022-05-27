package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeaterBlockEntity extends BloodMachineBlockEntity<HeaterBlockEntity>
{
    protected FurnaceAccessor accessor;

    protected HeaterBlockEntity(BlockEntityType<HeaterBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 2 * FluidConstants.BUCKET, 2 * FluidConstants.BUCKET);
    }

    public HeaterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.HEATER, pos, state);
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
        if (doWork(amount, transaction) == amount)
        {
            accessor.setBurnTime(2);
            world.setBlockState(pos.add(0, 1, 0), Blocks.DIRT.getDefaultState(), Block.NOTIFY_ALL);
        }
        else
        {
            world.setBlockState(pos.add(0, 1, 0), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
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
