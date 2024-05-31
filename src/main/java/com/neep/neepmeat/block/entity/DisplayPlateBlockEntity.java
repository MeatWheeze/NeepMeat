package com.neep.neepmeat.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.screen_handler.DisplayPlateScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class DisplayPlateBlockEntity extends SyncableBlockEntity implements ExtendedScreenHandlerFactory
{
    protected int capacity = 1;

    protected final WritableStackStorage storage;
    protected final MutateInPlace<ItemStack> mip = new Mutate();
    protected final Delegate delegate = new Delegate();

    public float stackRenderDelta; // Used by the renderer

    public DisplayPlateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new WritableStackStorage(this::sync, 1)
        {
            @Override
            protected void onFinalCommit()
            {
                super.onFinalCommit();
            }

            @Override
            protected long getCapacity(ItemVariant variant)
            {
                if (!variant.isBlank())
                {
                    return Math.min(variant.getItem().getMaxCount(), DisplayPlateBlockEntity.this.capacity);
                }
                return DisplayPlateBlockEntity.this.capacity;
            }
        };
    }

    public DisplayPlateBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ITEM_BUFFER_BLOCK_ENTITY, pos, state);
    }

    public WritableStackStorage getStorage(@Nullable Direction direction)
    {
        return storage;
    }

    public MutateInPlace<ItemStack> getMip(Void ctx)
    {
        return mip;
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        this.storage.writeNbt(tag);
        tag.putInt("capacity", capacity);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.storage.readNbt(nbt);
        this.capacity = nbt.getInt("capacity");
    }


    public void extractFromItem(ItemEntity itemEntity)
    {
        ItemStack itemStack = itemEntity.getStack();
        if (itemStack.isEmpty())
            return;

        try (Transaction transaction = Transaction.openOuter())
        {
            int transferred = (int) storage.insert(ItemVariant.of(itemStack), itemStack.getCount(), transaction);
            itemStack.decrement(transferred);
            if (itemStack.getCount() <= 0)
            {
                itemEntity.discard();
            }

            transaction.commit();
        }
    }

    @Override
    public Text getDisplayName()
    {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new DisplayPlateScreenHandler(playerInventory, syncId, delegate, capacity);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeInt(capacity);
    }

    protected class Mutate implements MutateInPlace<ItemStack>
    {
        @Override
        public ItemStack get()
        {
            return storage.getAsStack();
        }

        @Override
        public void set(ItemStack stack)
        {
            storage.setStack(stack);
        }
    }

    private class Delegate implements PropertyDelegate
    {

        @Override
        public int get(int index)
        {
            if (index == 0)
                return capacity;

            return 0;
        }

        @Override
        public void set(int index, int value)
        {
            if (index == 0)
                capacity = MathHelper.clamp(value, 0, 8);
        }

        @Override
        public int size()
        {
            return 1;
        }
    }
}
