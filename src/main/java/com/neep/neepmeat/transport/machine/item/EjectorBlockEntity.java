package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EjectorBlockEntity extends ItemPumpBlockEntity
{
    protected LazyBlockApiCache<Storage<ItemVariant>, Direction> insertionCache = LazyBlockApiCache.of(ItemStorage.SIDED,
            pos.offset(getCachedState().get(ItemPumpBlock.FACING).getOpposite()), this::getWorld,
            () -> getCachedState().get(ItemPumpBlock.FACING));
    protected LazyBlockApiCache<Storage<ItemVariant>, Direction> extractionCache = LazyBlockApiCache.of(ItemStorage.SIDED,
            pos.offset(getCachedState().get(ItemPumpBlock.FACING).getOpposite()), this::getWorld,
            () -> getCachedState().get(ItemPumpBlock.FACING));

    public EjectorBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.EJECTOR, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, EjectorBlockEntity be)
    {
        be.cooldown = Math.max(be.cooldown - 1, 0);

        if (be.shuttle > 0)
        {
            --be.shuttle;
            be.sync();
        }

        be.eject();

        if (be.cooldown == 0 && be.active && be.stored == null)
        {
            be.cooldown = 10;
            be.transferTick();
        }
    }

    private void transferTick()
    {
        Storage<ItemVariant> storage;
        if ((storage = extractionCache.find()) != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<ItemVariant> extractable = StorageUtil.findExtractableContent(storage, transaction);

                if (extractable == null)
                {
                    transaction.abort();
                    return;
                }

                long extracted = storage.extract(extractable.resource(), 1, transaction);


                if (extracted >= 1)
                {
                    succeed();
                    stored = new ResourceAmount<>(extractable.resource(), extracted);
                    transaction.commit();
                    return;
                }
                transaction.abort();
            }
        }
    }

    public long forwardToEntity(ResourceAmount<ItemVariant> resource, Transaction transaction)
    {
        BlockPos pos1 = getPos().offset(getCachedState().get(EjectorBlock.FACING));
        Box toBox = Box.of(Vec3d.ofCenter(pos1), 1, 1, 1);
        Vec3d centre = Vec3d.ofCenter(pos1);
        List<StorageMinecartEntity> toMinecarts = world.getEntitiesByType(TypeFilter.instanceOf(StorageMinecartEntity.class), toBox, (entity -> true));
        StorageMinecartEntity minecart = MiscUtils.closestEntity(toMinecarts, centre);
        if (minecart != null)
        {
            Storage<ItemVariant> storage = InventoryStorage.of(minecart, null);
            return storage.insert(resource.resource(), resource.amount(), transaction);
        }
        return 0;
    }

    public void markNeedsRefresh()
    {
    }
}
