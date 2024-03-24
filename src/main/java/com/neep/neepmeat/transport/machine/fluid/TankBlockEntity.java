package com.neep.neepmeat.transport.machine.fluid;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.network.TankMessagePacket;
import com.neep.neepmeat.transport.block.fluid_transport.TankBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class TankBlockEntity extends SyncableBlockEntity
{
    protected class InternalBuffer extends WritableSingleFluidStorage
    {
        public InternalBuffer(long capacity, Runnable finalCallback)
        {
            super(capacity, finalCallback);
        }

        @Override
        public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction)
        {
            TankBlockEntity upStorage = upCache.get().find(getType());
            long upExtracted = 0;
            if (upStorage != null)
            {
                upExtracted = upStorage.getStorage(null).extract(extractedVariant, maxAmount, transaction);
            }

            if (upExtracted < maxAmount)
            {
                long extracted = super.extract(extractedVariant, maxAmount - upExtracted, transaction);
                return upExtracted + extracted;
            }
            else
            {
                return upExtracted;
            }
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction)
        {
            var downStorage = downCache.get().find(getType());

            long downInserted = 0;
            if (downStorage != null)
            {
                downInserted = downStorage.getStorage(null).insert(insertedVariant, maxAmount, transaction);
            }

            long inserted = 0;
            if (maxAmount - downInserted > 0)
            {
                inserted = super.insert(insertedVariant, maxAmount - downInserted, transaction);
            }

            return downInserted + inserted;
        }
    };

    protected final InternalBuffer buffer;

    protected LazySupplier<BlockApiCache<TankBlockEntity, BlockEntityType<?>>> downCache = LazySupplier.of(() ->
            BlockApiCache.create(LOOKUP, (ServerWorld) getWorld(), getPos().down()));

    protected LazySupplier<BlockApiCache<TankBlockEntity, BlockEntityType<?>>> upCache = LazySupplier.of(() ->
            BlockApiCache.create(LOOKUP, (ServerWorld) getWorld(), getPos().up()));

    public TankBlockEntity(BlockEntityType type, BlockPos pos, BlockState state, long amount)
    {
        super(type, pos, state);
        buffer = new InternalBuffer(amount, this::sync);
    }

    public WritableSingleFluidStorage getStorage(Direction direction)
    {
        return buffer;
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.toNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNbt(tag);
    }

    @Override
    public void setStackNbt(ItemStack stack)
    {
        super.setStackNbt(stack);
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        if (WritableSingleFluidStorage.handleInteract(buffer, world, player, hand))
        {
            return true;
        }
        else if (!world.isClient())
        {
            showContents((ServerPlayerEntity) player, world, getPos(), buffer);
            return true;
        }
        return true;
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
    }

    public static void showContents(ServerPlayerEntity player, World world, BlockPos pos, StorageView<FluidVariant> buffer)
    {
        world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1.5f);
        TankMessagePacket.send(player, pos, buffer.getAmount(), buffer.getResource());
    }

    public static final BlockApiLookup<TankBlockEntity, BlockEntityType<?>> LOOKUP = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "tank_lookup"),
            TankBlockEntity.class, (Class<BlockEntityType<?>>) (Class<?>) BlockEntityType.class
    );

    public static <T extends BlockEntity> TankBlockEntity find(T be, BlockEntityType<?> type)
    {
        if (be.getType().equals(type) && be.getCachedState().get(TankBlock.AXIS).isVertical())
            return (TankBlockEntity) be;

        return null;
    }
}
