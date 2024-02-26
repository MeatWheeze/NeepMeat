package com.neep.neepmeat.machine.crafting_station;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.inventory.CombinedInventory;
import com.neep.neepmeat.screen_handler.DummyScreenHandler;
import com.neep.neepmeat.screen_handler.WorkstationScreenHandler;
import com.neep.neepmeat.util.ItemUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class WorkstationBlockEntity extends SyncableBlockEntity implements SidedInventory, NamedScreenHandlerFactory
{
    // Fool the CraftingInventory into sending contents updates to this block entity rather than an active screen handler
    protected DummyScreenHandler dummyScreenHandler = new DummyScreenHandler(this::onStackChanged);
    protected CraftingInventory input = new CraftingInventory(dummyScreenHandler, 3, 3);

    protected class Output implements ImplementedInventory
    {
        private DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
        private boolean inputsTaken;

        @Override
        public DefaultedList<ItemStack> getItems()
        {
            return items;
        }

        @Override
        public ItemStack removeStack(int slot, int count)
        {
            ItemStack retStack = ImplementedInventory.super.removeStack(slot, count);
            if (!inputsTaken)
            {
                inputsTaken = true;
                onTakeItem(world, input);
            }

            onStackChanged(input);
            return retStack;
        }

        public void reset(ItemStack stack)
        {
            this.inputsTaken = false;
            this.setStack(0, stack);
            this.markDirty();
        }
    }

    protected Output output = new Output();

    protected CombinedInventory combined = new CombinedInventory(input, output);

    public WorkstationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public WorkstationBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.WORKSTATION, pos, state);
    }

    @Override
    public int size()
    {
        return combined.size();
    }

    @Override
    public boolean isEmpty()
    {
        return combined.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return combined.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int count)
    {
        return combined.removeStack(slot, count);
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        return combined.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        combined.setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public int[] getAvailableSlots(Direction side)
    {
        return side == Direction.DOWN ? new int[]{9} : IntStream.range(0, 8).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
    {
        return slot != 9;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir)
    {
        return true;
    }

    @Override
    public Text getDisplayName()
    {
        return Text.translatable("container." + NeepMeat.NAMESPACE + ".workstation");
    }

    public void onStackChanged(Inventory inventory)
    {
        if (world == null) return;
        CraftingRecipe recipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, world).orElse(null);
        if (!output.inputsTaken || output.isEmpty() && output.inputsTaken)
        {
            if (recipe != null)
            {
                output.reset(recipe.craft(input, world.getRegistryManager()));
            }
            else output.setStack(0, ItemStack.EMPTY);
        }
        output.markDirty();
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.put("input", ItemUtil.toNbt(input));
        nbt.put("output", ItemUtil.toNbt(output));
        nbt.putBoolean("inputsTaken", output.inputsTaken);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        ItemUtil.readInventory(nbt.getCompound("input"), input);
        ItemUtil.readInventory(nbt.getCompound("output"), output);
        this.output.inputsTaken = nbt.getBoolean("inputsTaken");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new WorkstationScreenHandler(syncId, inv, input, output, combined);
    }

    @Override
    public void clear()
    {
        combined.clear();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
    }

    public static void onTakeItem(World world, CraftingInventory input)
    {
        DefaultedList<ItemStack> defaultedList = world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, input, world);
        for (int i = 0; i < defaultedList.size(); ++i)
        {
            ItemStack currentStack = input.getStack(i);
            ItemStack remainderStack = defaultedList.get(i);
            if (!currentStack.isEmpty())
            {
                input.removeStack(i, 1);
                currentStack = input.getStack(i);
            }
            if (remainderStack.isEmpty()) continue;
            if (currentStack.isEmpty())
            {
                input.setStack(i, remainderStack);
                continue;
            }
            if (ItemStack.areItemsEqualIgnoreDamage(currentStack, remainderStack) && ItemStack.areNbtEqual(currentStack, remainderStack))
            {
                remainderStack.increment(currentStack.getCount());
                input.setStack(i, remainderStack);
            }
        }
    }
}
