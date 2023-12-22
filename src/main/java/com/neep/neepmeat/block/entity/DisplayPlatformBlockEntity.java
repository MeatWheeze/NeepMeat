package com.neep.neepmeat.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.component.TableComponent;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class DisplayPlatformBlockEntity extends SyncableBlockEntity
{
    protected final WritableStackStorage storage;
    protected final TableComponent<ItemVariant> tableComponent = new Component();
    protected final MutateInPlace<ItemStack> mip = new Mutate();

    public float stackRenderDelta; // Used by the renderer

    public DisplayPlatformBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new WritableStackStorage(this::sync, 1)
        {
            @Override
            protected void onFinalCommit()
            {
                super.onFinalCommit();
            }
        };
    }

    public DisplayPlatformBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ITEM_BUFFER_BLOCK_ENTITY, pos, state);
    }

    public WritableStackStorage getStorage(@Nullable Direction direction)
    {
        return storage;
    }

    public TableComponent<ItemVariant> getTableComponent(Void ctx)
    {
        return tableComponent;
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
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        this.storage.readNbt(tag);
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


    protected class Component implements TableComponent<ItemVariant>
    {
        @Override
        public Storage<ItemVariant> getStorage()
        {
            return DisplayPlatformBlockEntity.this.getStorage(null);
        }

        @Override
        public Identifier getType()
        {
            return RecipeInputs.ITEM_ID;
        }
    };

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
}
