package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.machine.EjectorBlock;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUitls;
import com.neep.neepmeat.util.RetrievalTarget;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EjectorBlockEntity extends ItemPumpBlockEntity
{
    protected List<RetrievalTarget<ItemVariant>> retrievalCache = new ArrayList<>();
    protected BlockApiCache<Storage<ItemVariant>, Direction> insertionCache;
    protected BlockApiCache<Storage<ItemVariant>, Direction> extractionCache;

    public EjectorBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.EJECTOR, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, EjectorBlockEntity be)
    {
        be.cooldown = Math.max(be.cooldown - 1, 0);

        if (be.needsRefresh)
        {
            Direction face = state.get(ItemPumpBlock.FACING).getOpposite();
            updateRetrievalCache((ServerWorld) world, pos, face, be);
        }

        if (be.shuttle > 0)
        {
            --be.shuttle;
            be.sync();
        }

        if (be.cooldown == 0 && be.active)
        {
            be.cooldown = 10;

            Direction facing = state.get(BaseFacingBlock.FACING);
            Storage<ItemVariant> storage;

            if ((storage = be.extractionCache.find(facing)) != null)
            {
                Transaction transaction = Transaction.openOuter();
                ResourceAmount<ItemVariant> extractable = StorageUtil.findExtractableContent(storage, transaction);

                if (extractable == null)
                {
                    transaction.abort();
                    return;
                }

                long extracted = storage.extract(extractable.resource(), 1, transaction);

                long forwarded;
                ResourceAmount<ItemVariant> resourceAmount = new ResourceAmount<>(extractable.resource(), extracted);

                // Try pipes
                forwarded = be.forwardItem(resourceAmount, transaction);

                // Try entities
                if (forwarded != -1)
                {
                    forwarded = be.forwardToEntity(resourceAmount, transaction);
                }

                if (forwarded != -1)
                {
                    transaction.abort();
                    return;
                }
                be.succeed();
                transaction.commit();
            }
        }
    }

    public long forwardToEntity(ResourceAmount<ItemVariant> resource, Transaction transaction)
    {
        BlockPos pos1 = getPos().offset(getCachedState().get(EjectorBlock.FACING));
        Box toBox = Box.of(Vec3d.ofCenter(pos1), 1, 1, 1);
        Vec3d centre = Vec3d.ofCenter(pos1);
        List<StorageMinecartEntity> toMinecarts = world.getEntitiesByType(TypeFilter.instanceOf(StorageMinecartEntity.class), toBox, (entity -> true));
        StorageMinecartEntity minecart = MiscUitls.closestEntity(toMinecarts, centre);
        if (minecart != null)
        {
            Storage<ItemVariant> storage = InventoryStorage.of(minecart, null);
            return storage.insert(resource.resource(), resource.amount(), transaction);
        }
        return 0;
    }

    public void markNeedsRefresh()
    {
        this.needsRefresh = true;
    }

    public void updateRedstone(boolean redstone)
    {
        this.active = redstone;
    }

    public static void updateRetrievalCache(ServerWorld world, BlockPos pos, Direction face, EjectorBlockEntity be)
    {
        be.retrievalCache = MiscUitls.floodSearch(pos, face, world, pair -> ItemStorage.SIDED.find(world, pair.getLeft(), pair.getRight()) != null, 16);
        be.insertionCache = BlockApiCache.create(ItemStorage.SIDED, world, pos.offset(face.getOpposite()));
        be.extractionCache = BlockApiCache.create(ItemStorage.SIDED, world, pos.offset(face));
        be.needsRefresh = false;
    }

    public long canForward(ResourceAmount<ItemVariant> amount, Transaction transaction)
    {
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);
        Storage<ItemVariant> storage;
        if (insertionCache != null && (storage = insertionCache.find(facing)) != null)
        {
            return storage.simulateInsert(amount.resource(), amount.amount(), transaction);
        }
        return amount.amount();
    }

//    @Override
//    public void fromClientTag(NbtCompound tag)
//    {
//        super.fromClientTag(tag);
//    }
//
//    @Override
//    public NbtCompound toClientTag(NbtCompound tag)
//    {
//        super.toClientTag(tag);
//        return tag;
//    }
//
//    @Override
//    public void writeNbt(NbtCompound tag)
//    {
//        super.writeNbt(tag);
//    }
//
//    @Override
//    public void readNbt(NbtCompound tag)
//    {
//        super.readNbt(tag);
//    }
}
