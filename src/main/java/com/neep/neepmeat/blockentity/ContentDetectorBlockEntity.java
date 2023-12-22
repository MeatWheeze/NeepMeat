package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.block.BufferBlock;
import com.neep.neepmeat.block.ContentDetectorBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.inventory.ContentDetectorInventory;
import com.neep.neepmeat.inventory.ImplementedInventory;
import com.neep.neepmeat.screen_handler.BufferScreenHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("UnstableApiUsage")
public class ContentDetectorBlockEntity extends BlockEntity implements
        Storage<ItemVariant>,
        NamedScreenHandlerFactory
{
    public static final String NBT_POWERED = "powered";

    public ImplementedInventory inventory = new ContentDetectorInventory();

    protected BlockApiCache<Storage<ItemVariant>, Direction> cache;
    protected boolean powered;

    public ContentDetectorBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.CONTENT_DETECTOR, pos, state);
    }

    public ContentDetectorBlockEntity(BlockEntityType<ContentDetectorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        inventory.writeNbt(tag);
        tag.putBoolean(NBT_POWERED, powered);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        inventory.readNbt(tag);
        powered = tag.getBoolean(NBT_POWERED);
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
        return new BufferScreenHandler(syncId, inv, this.inventory);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.inventory, Direction.UP);
        return storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.inventory, Direction.UP);
        if (getCachedState().get(BufferBlock.POWERED))
        {
            return 0;
        }
        return storage.extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.inventory, Direction.UP);
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

    public static ItemStack mutateView(StorageView<ItemVariant> view)
    {
        return view.getResource().toStack((int) view.getAmount());
//        return Items.STONE.getDefaultStack();
    }

    public static boolean validStack(ItemStack stack)
    {
        return !stack.isEmpty();
    }

    public static boolean containsStack(List<ItemStack> list, ItemStack stack)
    {
        for (ItemStack itemStack : list)
        {
            if (ItemStack.areEqual(stack, itemStack))
            {
                return true;
            }
        }
        return false;
    }

    public boolean observeStorage()
    {
        Direction facing = getCachedState().get(ContentDetectorBlock.FACING);
        Storage<ItemVariant> observedStorage;
        if (cache != null && (observedStorage = cache.find(facing.getOpposite())) != null)
        {
            Transaction transaction = Transaction.openOuter();

            List<ItemStack> filterStacks = inventory.getItems().stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
            List<ItemStack> observedStacks = StreamSupport.stream(observedStorage.iterable(transaction).spliterator(), false)
                    .map(ContentDetectorBlockEntity::mutateView)
                    .filter(ContentDetectorBlockEntity::validStack)
                    .collect(Collectors.toList());

            int size = observedStacks.stream().filter(stack -> ContentDetectorBlockEntity.containsStack(filterStacks, stack)).collect(Collectors.toList()).size();
            powered = size > 0;

            transaction.commit();
        }
        return powered;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ContentDetectorBlockEntity be)
    {
        if ((world.getTime() % 20) != 0)
            return;

        if (be.cache == null)
            be.refreshCache();

        be.observeStorage();
        world.setBlockState(pos, state.with(ContentDetectorBlock.POWERED, be.powered));
    }
}