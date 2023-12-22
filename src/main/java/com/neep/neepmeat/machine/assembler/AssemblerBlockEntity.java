package com.neep.neepmeat.machine.assembler;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.FakePlayerEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.screen_handler.AssemblerScreenHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
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

public class AssemblerBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory
{
    public static final int MAX_PROGRESS = 10;
    public static final float MAX_INCREMENT = 1f;
    public static final float MIN_INCREMENT = 0.3f;
    protected float progress;
    protected float increment = 1;

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
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0: storage.outputSlots = value; return;
                case 1: targetSize = value; return;

            }
        }

        @Override
        public int size()
        {
            return 2;
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

    public void tick(ServerWorld world)
    {
        progress = Math.min(MAX_PROGRESS, progress + increment);

        BlockPos down = pos.down();
        if (cache == null)
        {
            cache = BlockApiCache.create(ItemStorage.SIDED, world, down);
        }

        if (progress >= MAX_PROGRESS && cache.getBlockEntity() instanceof SidedInventory target)
        {
            Inventory inventory = storage.getInventory();
            for (int i = 0; i < target.size() && i < 12; ++i)
            {
                ItemStack patternStack = inventory.getStack(i);
                if (target.getStack(i).isEmpty() && !patternStack.isEmpty() && target.canInsert(i, patternStack, null))
                {
                    ItemStack transferStack = storage.findIngredient(patternStack);
                    if (!transferStack.isEmpty())
                    {
                        target.setStack(i, patternStack.copy());
                        progress = 0;
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
}
