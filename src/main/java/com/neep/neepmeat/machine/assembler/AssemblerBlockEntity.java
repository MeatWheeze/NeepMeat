package com.neep.neepmeat.machine.assembler;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.entity.FakePlayerEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.screen_handler.AssemblerScreenHandler;
import com.neep.neepmeat.util.ItemUtils;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public class AssemblerBlockEntity extends BloodMachineBlockEntity implements NamedScreenHandlerFactory
{
    public static final int PATTERN_SLOTS = 12;
    public static final int MAX_PROGRESS = 20;
    public static final float MAX_INCREMENT = 1f;
    public static final float MIN_INCREMENT = 0.3f;
    protected float progress;
    protected float increment = 1;
    protected boolean slotSelectMode;

    BlockApiCache<Storage<ItemVariant>, Direction> cache;

    protected AssemblerStorage storage;

    protected int targetSize;

    protected PropertyDelegate delegate = new PropertyDelegate()
    {
        @Override
        public int get(int index)
        {
            return switch (index)
            {
                case 0 -> storage.outputSlots;
                case 1 -> targetSize;
                case 2 -> slotSelectMode ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0: storage.outputSlots = value; break;
                case 1: targetSize = value; break;
                case 2: slotSelectMode = value > 0; break;
            }
            markDirty();
        }

        @Override
        public int size()
        {
            return 3;
        }
    };

    public AssemblerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new AssemblerStorage(this);
    }

    public AssemblerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ASSEMBLER, pos, state);
    }

    public void test()
    {

        BlockPos down = pos.down();

        if (!(world instanceof ServerWorld serverWorld))
            return;

        NamedScreenHandlerFactory factory = world.getBlockState(pos.down()).createScreenHandlerFactory(world, pos);
        FakePlayerEntity fakePlayer = new FakePlayerEntity(world.getServer(), serverWorld, pos);
        if (factory != null)
        {
            fakePlayer.openHandledScreen(factory);
            ScreenHandler handler = fakePlayer.currentScreenHandler;
            handler.getSlot(1).setStack(Items.STONE.getDefaultStack());
        }
        fakePlayer.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText("container." + NeepMeat.NAMESPACE + ".assembler");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity player)
    {
        Inventory targetInv = new SimpleInventory(12);
        BlockEntity be = cache.getBlockEntity();
        if (be instanceof Inventory inv)
        {
            targetInv = inv;
            delegate.set(1, inv.size());
        }
        return new AssemblerScreenHandler(syncId, playerInv, storage.getInventory(), targetInv, delegate);
    }

    public void update()
    {
        if (world instanceof ServerWorld serverWorld)
            cache = BlockApiCache.create(ItemStorage.SIDED, serverWorld, pos.down());
    }

    // Move items from target output slots to internal output buffer
    public boolean removeOutputs(Inventory target)
    {
        for (int i = 0; i < target.size() && i < PATTERN_SLOTS; ++i)
        {
//            if (((delegate.get(0) >> i) & 1) == 1)
            if (storage.isOutput(delegate, i))
            {
                if (ItemUtils.insertItem(target.getStack(i), storage.getInventory(), 24, 28, false))
                    return true;
            }
        }
        return false;
    }

    public void tick(ServerWorld world)
    {
        progress = Math.min(MAX_PROGRESS, progress + increment);

        BlockPos down = pos.down();
        if (cache == null)
        {
            cache = BlockApiCache.create(ItemStorage.SIDED, world, down);
        }

        if (progress >= MAX_PROGRESS && cache.getBlockEntity() instanceof Inventory target)
        {
            progress = 0;

            if (removeOutputs(target))
            {
                return;
            }

            Inventory inventory = storage.getInventory();
            for (int i = 0; i < target.size() && i < PATTERN_SLOTS; ++i)
            {
                ItemStack patternStack = inventory.getStack(i);
                if (!storage.isOutput(delegate, i) && target.getStack(i).isEmpty() && !patternStack.isEmpty())
                {
                    // Honour valid insertion slots
                    if (target instanceof SidedInventory sided && !sided.canInsert(i, patternStack, null))
                    {
                        break;
                    }

                    ItemStack transferStack = storage.findIngredient(patternStack);
                    if (!transferStack.isEmpty())
                    {
                        target.setStack(i, patternStack.copy());
                        break;
                    }
                }
            }
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, AssemblerBlockEntity be)
    {
        be.tick((ServerWorld) world);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }

    public AssemblerStorage getStorage()
    {
        return storage;
    }

    public Storage<FluidVariant> getFluidStorage(Direction dir)
    {
        Direction facing = getCachedState().get(AssemblerBlock.FACING);
        if (dir == facing.rotateYClockwise() || dir == facing.rotateYCounterclockwise())
            return inputStorage;

        return null;
    }

    @Override
    public Storage<FluidVariant> getBuffer(Direction direction)
    {
        return null;
    }
}
