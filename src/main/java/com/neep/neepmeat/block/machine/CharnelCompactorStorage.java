package com.neep.neepmeat.block.machine;

import com.google.common.collect.MapMaker;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.util.math.Direction.UP;

// Most of this is copied from ComposterWrapper. It looks awful, I'm never doing this again.
@SuppressWarnings("UnstableApiUsage")
public class CharnelCompactorStorage extends SnapshotParticipant<Float>
{
    private static final Map<WorldLocation, CharnelCompactorStorage> CHARNEL_STORAGES = new MapMaker().concurrencyLevel(1).weakValues().makeMap();
    private static final ItemVariant OUTPUT = ItemVariant.of(NMItems.CRUDE_INTEGRATION_CHARGE);

    public record WorldLocation(World world, BlockPos pos)
    {
        private BlockState getBlockState()
        {
            return world.getBlockState(pos);
        }

        private void setBlockState(BlockState state)
        {
            world.setBlockState(pos, state);
        }
    }

    @Nullable
    public static Storage<ItemVariant> getStorage(World world, BlockPos pos, Direction direction)
    {
        Objects.requireNonNull(direction);
        if (direction.getAxis().isVertical())
        {
            WorldLocation location = new WorldLocation(world, pos.toImmutable());
            CharnelCompactorStorage storage = CHARNEL_STORAGES.computeIfAbsent(location, CharnelCompactorStorage::new);
            return direction == UP ? storage.upStorage : storage.downStorage;
        }
        else
        {
            return null;
        }
    }

    private static final float DO_NOTHING = 0f;
    private static final float EXTRACT_OUTPUT = -1f;

    private final WorldLocation location;
    // -1 if bonemeal was extracted, otherwise the composter increase probability of the (pending) inserted item.
    private Float increaseProbability = DO_NOTHING;
    private final TopStorage upStorage = new TopStorage();
    private final BottomStorage downStorage = new BottomStorage();

    private CharnelCompactorStorage(WorldLocation location)
    {
        this.location = location;
    }

    @Override
    protected Float createSnapshot()
    {
        return increaseProbability;
    }

    @Override
    protected void readSnapshot(Float snapshot)
    {
        // Reset after unsuccessful commit.
        increaseProbability = snapshot;
    }

    @Override
    protected void onFinalCommit()
    {
        // Apply pending action
        if (increaseProbability == EXTRACT_OUTPUT)
        {
            extractOutput(location, false);
        }
        else if (increaseProbability > 0)
        {
            boolean increaseSuccessful = location.world.getRandom().nextDouble() < increaseProbability;

            if (increaseSuccessful)
            {
//                BlockState state = location.getBlockState();
//                int newLevel = state.get(CharnelCompactorBlock.LEVEL) + 1;
//                BlockState newState = state.with(CharnelCompactorBlock.LEVEL, newLevel);
//                location.setBlockState(newState);
//
//                if (newLevel == 7)
//                {
//                    location.world.getBlockTickScheduler().schedule(location.pos, state.getBlock(), 20);
//                }
                addLevel(location);
            }
        }

        // Reset after successful commit.
        increaseProbability = DO_NOTHING;
    }

    public static void extractOutput(WorldLocation location, boolean spawnItem)
    {
        location.setBlockState(location.getBlockState().with(CharnelCompactorBlock.LEVEL, 0));
        World world = location.world;
        BlockPos pos = location.pos;
        if (spawnItem)
        {
            double d = (world.random.nextFloat() * 0.7f) + 0.15f;
            double e = (world.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
            double g = (world.random.nextFloat() * 0.7f) + 0.15f;
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + d, pos.getY() + e, pos.getZ() + g, OUTPUT.toStack(1));
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
            world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    public static void addLevel(WorldLocation location)
    {
        BlockState state = location.getBlockState();
        int newLevel = state.get(CharnelCompactorBlock.LEVEL) + 1;
        BlockState newState = state.with(CharnelCompactorBlock.LEVEL, newLevel);
        location.setBlockState(newState);

        if (newLevel == 7)
        {
            location.world.getBlockTickScheduler().schedule(location.pos, state.getBlock(), 20);
        }

    }

    private class TopStorage implements InsertionOnlyStorage<ItemVariant>
    {
        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
        {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);

            // Check amount.
            if (maxAmount < 1) return 0;
            // Check that no action is scheduled.
            if (increaseProbability != DO_NOTHING) return 0;
            // Check that the composter can accept items.
            if (location.getBlockState().get(CharnelCompactorBlock.LEVEL) >= 7) return 0;
            // Check that the item is compostable.
            float insertedIncreaseProbability = CharnelCompactorBlock.getIncreaseChance(resource.getItem());
            if (insertedIncreaseProbability <= 0) return 0;

            // Schedule insertion.
            updateSnapshots(transaction);
            increaseProbability = insertedIncreaseProbability;
            return 1;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
        {
            return Collections.emptyIterator();
        }
    }

    private class BottomStorage implements ExtractionOnlyStorage<ItemVariant>, SingleSlotStorage<ItemVariant>
    {

        private boolean hasOutput()
        {
            // We only have bone meal if the level is 8 and no action was scheduled.
            return increaseProbability == DO_NOTHING && location.getBlockState().get(CharnelCompactorBlock.LEVEL) == 8;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
        {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);

            // Check amount.
            if (maxAmount < 1) return 0;
            // Check that the resource is bone meal.
            if (!OUTPUT.equals(resource)) return 0;
            // Check that there is bone meal to extract.
            if (!hasOutput()) return 0;

            updateSnapshots(transaction);
            increaseProbability = EXTRACT_OUTPUT;
            return 1;
        }

        @Override
        public boolean isResourceBlank()
        {
            return getResource().isBlank();
        }

        @Override
        public ItemVariant getResource()
        {
            return OUTPUT;
        }

        @Override
        public long getAmount()
        {
            return hasOutput() ? 1 : 0;
        }

        @Override
        public long getCapacity()
        {
            return 1;
        }
    }
}