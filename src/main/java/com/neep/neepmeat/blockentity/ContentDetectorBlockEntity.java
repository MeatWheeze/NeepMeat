package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.block.item_transport.BufferBlock;
import com.neep.neepmeat.block.content_detector.ContentDetectorBehaviour;
import com.neep.neepmeat.block.content_detector.ContentDetectorBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.inventory.ContentDetectorInventory;
import com.neep.neepmeat.inventory.ImplementedInventory;
import com.neep.neepmeat.screen_handler.ContentDetectorScreenHandler;
import com.neep.neepmeat.util.FilterUtils;
import com.neep.neepmeat.util.ItemUtils;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("UnstableApiUsage")
public class ContentDetectorBlockEntity extends BlockEntity implements
        Storage<ItemVariant>,
        NamedScreenHandlerFactory
{
    public static final String NBT_POWERED = "powered";
    public static final String NBT_MODE = "mode";
    public static final String NBT_COUNT_MODE = "count_mode";
    public static final String NBT_BEHAVIOUR_MODE = "behaviour_mode";

    public ImplementedInventory filterInventory = new ContentDetectorInventory();

    protected BlockApiCache<Storage<ItemVariant>, Direction> cache;
    protected boolean powered;
    protected int mode = 0;
    protected int countMode = 0; // 0: Any amount, 1: Greater in filter, 2: Greater in storage, 3: Equal
    protected int behaviourMode = 0; // 0: Regulate, 1: Absolute

    private final PropertyDelegate modeDelegate = new PropertyDelegate()
    {
        @Override
        public int get(int index)
        {
            return switch (index)
            {
                case ContentDetectorBehaviour.DEL_COUNT -> countMode;
                case ContentDetectorBehaviour.DEL_BEHAVIOUR -> behaviourMode;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case ContentDetectorBehaviour.DEL_COUNT -> countMode = value;
                case ContentDetectorBehaviour.DEL_BEHAVIOUR -> behaviourMode = value;
            }
        }

        @Override
        public int size()
        {
            return 2;
        }
    };

    public ContentDetectorBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.CONTENT_DETECTOR, pos, state);
    }

    public ContentDetectorBlockEntity(BlockEntityType<ContentDetectorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        filterInventory.writeNbt(tag);
        tag.putBoolean(NBT_POWERED, powered);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_COUNT_MODE, countMode);
        tag.putInt(NBT_BEHAVIOUR_MODE, behaviourMode);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        filterInventory.readNbt(tag);
        powered = tag.getBoolean(NBT_POWERED);
        mode = tag.getInt(NBT_MODE);
        countMode = tag.getInt(NBT_COUNT_MODE);
        behaviourMode = tag.getInt(NBT_BEHAVIOUR_MODE);
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new ContentDetectorScreenHandler(syncId, inv, this.filterInventory, modeDelegate);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.filterInventory, Direction.UP);
        return storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.filterInventory, Direction.UP);
        if (getCachedState().get(BufferBlock.POWERED))
        {
            return 0;
        }
        return storage.extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.filterInventory, Direction.UP);
        return storage.iterator(transaction);
    }

    public void refreshCache()
    {
        if (world instanceof ServerWorld serverWorld)
        {
            Direction facing = getCachedState().get(ContentDetectorBlock.FACING);
            cache = BlockApiCache.create(ItemStorage.SIDED, serverWorld, pos.offset(facing));
        }
    }

    public boolean observeStorage()
    {
        Direction facing = getCachedState().get(ContentDetectorBlock.FACING);
        Storage<ItemVariant> observedStorage;
        if (cache != null && (observedStorage = cache.find(facing.getOpposite())) != null)
        {
            Transaction transaction = Transaction.openOuter();

            // Get a list of ItemVariants in the filter with no duplicates
            List<ItemVariant> variants = StreamSupport.stream(InventoryStorage.of(filterInventory, facing).iterable(transaction).spliterator(), false)
                    .filter(ItemUtils::notBlank)
                    .map(StorageView::getResource)
                    .distinct()
                    .collect(Collectors.toList());

            // Determine the check to perform
            FilterUtils.Filter filter = switch (countMode)
                    {
                        case ContentDetectorBehaviour.STORAGE_GREATER -> ((obs, filt) -> obs < filt);
                        case ContentDetectorBehaviour.STORAGE_LESS -> ((obs, filt) -> obs > filt);
                        case ContentDetectorBehaviour.STORAGE_EQUALS -> ((obs, filt) -> obs == filt);
                        default -> ((obs, filt) -> true);
                    };

            List<?> filtered = variants.stream().filter(variant ->
            {
                Optional<Long> observedView = ItemUtils.totalAmount(InventoryStorage.of(filterInventory, facing.getOpposite()), variant, transaction);
                Optional<Long> filterView = ItemUtils.totalAmount(observedStorage, variant, transaction);

                if (observedView.isPresent() && filterView.isPresent())
                {
                    return filter.test(filterView.get(), observedView.get());
                }
                return false;
            }).collect(Collectors.toList());

            int size = filtered.size();
            int size2 = StreamSupport.stream(observedStorage.iterable(transaction).spliterator(), false)
                    .map(StorageView::getResource)
                    .filter(FilterUtils.containsVariant(variants))
                    .collect(Collectors.toList()).size();

            if (behaviourMode == 0)
            {
                if (mode == 0) // Waiting for stacks to arrive
                {
                    if (size == variants.size() && size != 0)
                    {
                        powered = true;
                        mode = 1;
                    }
                }
                else if (mode == 1) // Waiting for stacks to leave
                {
                    if (size2 == 0)
                    {
                        powered = false;
                        mode = 0;
                    }
                }
            }
            else if (behaviourMode == 1)
            {
                powered = size == variants.size();
            }

            transaction.commit();
        }
        return powered;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ContentDetectorBlockEntity be)
    {
        if ((world.getTime() % 10) != 0)
            return;

        if (be.cache == null)
            be.refreshCache();

        be.observeStorage();
        world.setBlockState(pos, state.with(ContentDetectorBlock.POWERED, be.powered));
    }

}