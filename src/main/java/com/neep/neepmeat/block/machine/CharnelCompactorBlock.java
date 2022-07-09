package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.impl.transfer.item.ComposterWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CharnelCompactorBlock extends BaseBlock implements InventoryProvider
{
    public static final IntProperty LEVEL = Properties.LEVEL_8;

    public CharnelCompactorBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    public static float getIncreaseChance(ItemConvertible item)
    {
        return 1;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if (state.get(LEVEL) == 7)
        {
            world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (state.get(LEVEL) == 7)
        {
            world.setBlockState(pos, state.cycle(LEVEL), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos)
    {
        int level = state.get(LEVEL);
        if (level == 8)
        {
            return new Inventory(state, world, pos, new ItemStack(NMItems.CRUDE_INTEGRATION_CHARGE));
        }
        if (level < 7)
        {
            return new Inventory(state, world, pos, ItemStack.EMPTY);
        }
        return new DummyInventory();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos)
    {
        return state.get(LEVEL);
    }

    public static class Inventory extends SimpleInventory implements SidedInventory
    {

        private final BlockState state;
        private final WorldAccess world;
        private final BlockPos pos;
        private boolean dirty;

        public Inventory(BlockState state, WorldAccess world, BlockPos pos, @NotNull ItemStack outputItem)
        {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;

            if (!outputItem.isEmpty())
            {
                this.setStack(0, outputItem);
            }
        }

        @Override
        public int getMaxCountPerStack()
        {
            return 1;
        }

        @Override
        public int[] getAvailableSlots(Direction side)
        {
            int[] nArray;
            if (side == Direction.DOWN && state.get(LEVEL) == 8 || side == Direction.UP && state.get(LEVEL) < 8)
            {
                int[] nArray2 = new int[1];
                nArray = nArray2;
            }
            else
            {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
        {
            boolean out = !this.dirty
                    && state.get(LEVEL) != 8
                    && dir == Direction.UP
                    && getIncreaseChance(stack.getItem()) > 0
                    && state.get(LEVEL) < 8;
            out = out;
            return out;
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir)
        {
            return !this.dirty
                && this.state.get(LEVEL) == 8
                && stack.isOf(NMItems.CRUDE_INTEGRATION_CHARGE);
        }

        @Override
        public void markDirty()
        {
            ItemStack stack = this.getStack(0);
            if (this.state.get(LEVEL) == 8 && stack.isEmpty())
            {
                empty(this.state, this.world, this.pos);
                this.dirty = true;
                return;
            }

            if (!stack.isEmpty() && this.state.get(LEVEL) < 8)
            {
                this.dirty = true;
                BlockState blockState = CharnelCompactorBlock.addLevel(this.state, this.world, this.pos, stack);
                this.removeStack(0);
            }
        }
    }

    static class DummyInventory extends SimpleInventory implements SidedInventory
    {
        public DummyInventory()
        {
            super(0);
        }

        @Override
        public int[] getAvailableSlots(Direction side)
        {
            return new int[0];
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
        {
            return false;
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir)
        {
            return false;
        }
    }

    private static BlockState addLevel(BlockState state, WorldAccess world, BlockPos pos, ItemStack stack)
    {
        int level = state.get(LEVEL);
        float chance = getIncreaseChance(stack.getItem());
        if (level == 0 && chance > 0.0f || world.getRandom().nextDouble() < chance)
        {
            int j = level + 1;
            BlockState blockState = state.with(LEVEL, j);
            world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
            if (j == 7)
            {
                world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
            }
            return blockState;
        }
        return state;
    }

    static BlockState empty(BlockState state, WorldAccess world, BlockPos pos)
    {
        BlockState blockState = state.with(LEVEL, 0);
        world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        return blockState;
    }

    static
    {
//        ItemStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> ComposterWrapper.get(world, pos, direction), Blocks.COMPOSTER);
    }
}
