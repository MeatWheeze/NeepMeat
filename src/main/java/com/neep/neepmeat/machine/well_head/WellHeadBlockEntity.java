package com.neep.neepmeat.machine.well_head;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WellHeadBlockEntity extends SyncableBlockEntity
{
//    private final WritableSingleFluidStorage fluidStorage = new WritableSingleFluidStorage(8 * FluidConstants.BUCKET, this::markDirty);
    private final LazyBlockApiCache<Storage<FluidVariant>, Direction> outputCache;
    private final FluidPump pump = FluidPump.of(-0.5f, () -> AcceptorModes.PUSH, true);

    public WellHeadBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        outputCache = LazyBlockApiCache.of(FluidStorage.SIDED, pos.offset(state.get(WellHeadBlock.FACING)), this::getWorld, () -> state.get(WellHeadBlock.FACING));
    }

    public void serverTick()
    {
    }

    public void receiveFluid(long distributeAmount, TransactionContext transaction)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            Direction facing = getCachedState().get(WellHeadBlock.FACING);
            for (ItemStack stack : RandomOres.random(serverWorld, pos))
            {
                ItemPipeUtil.stackToAny(serverWorld, pos, facing, ItemVariant.of(stack), stack.getCount(), transaction);
            }
        }
    }

    public FluidPump getFluidPump(Direction face)
    {
        if (face == getCachedState().get(WellHeadBlock.FACING))
        {
            return pump;
        }
        return null;
    }

    public Storage<FluidVariant> getFluidStorage(Direction face)
    {
        if (face == getCachedState().get(WellHeadBlock.FACING))
        {
//            return fluidStorage;
        }
        return null;
    }
}
