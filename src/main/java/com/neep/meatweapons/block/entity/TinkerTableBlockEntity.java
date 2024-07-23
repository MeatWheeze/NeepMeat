package com.neep.meatweapons.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.meatweapons.screen.TinkerTableScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TinkerTableBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory
{
    private final ImplementedInventory inventory = new ImplementedInventory()
    {
        private DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

        @Override
        public DefaultedList<ItemStack> getItems()
        {
            return items;
        }

        @Override
        public void markDirty()
        {
            TinkerTableBlockEntity.this.markDirty();
        }
    };

    public TinkerTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
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
        return new TinkerTableScreenHandler(syncId, playerInventory, inventory, getPos());
    }
}
