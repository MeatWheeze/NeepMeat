package com.neep.neepmeat.neepbus;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SenderTestBlock extends BaseBlock implements BlockEntityProvider, DataCable, NeepBusProvider
{
    public SenderTestBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof BlockEntity be)
        {
            be.sendTest();
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public net.minecraft.block.entity.BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NeepBusBlocks.SENDER_TEST_BE.instantiate(pos, state);
    }

    @Override
    public Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state)
    {
        return NO_PORTS;
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {
        if (world.getBlockEntity(pos) instanceof BlockEntity be)
        {
            be.cache.get().clear();
        }
    }

    public static class BlockEntity extends SyncableBlockEntity
    {
        private final LazySupplier<CachingSender> cache = LazySupplier.of(() -> new CachingSender(getWorld(), getPos()));

        private int count = 0;

        public BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public void sendTest()
        {
            cache.get().send("ooer", count);
            count++;
        }
    }
}
