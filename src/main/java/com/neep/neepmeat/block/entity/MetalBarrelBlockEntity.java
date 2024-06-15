package com.neep.neepmeat.block.entity;

import com.neep.neepmeat.block.MetalBarrelBlock;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetalBarrelBlockEntity extends LootableContainerBlockEntity
{
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private final ViewerCountManager stateManager = new ViewerCountManager(){

        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state)
        {
            if (state.get(MetalBarrelBlock.TYPE) != MetalBarrelBlock.Type.BOTTOM)
                MetalBarrelBlockEntity.this.playSound(state, NMSounds.METAL_BARREL_OPEN);

            MetalBarrelBlockEntity.this.setOpen(state, true);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state)
        {
            if (state.get(MetalBarrelBlock.TYPE) != MetalBarrelBlock.Type.BOTTOM)
                MetalBarrelBlockEntity.this.playSound(state, NMSounds.METAL_BARREL_CLOSE);

            MetalBarrelBlockEntity.this.setOpen(state, false);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount)
        {
        }

        @Override
        protected boolean isPlayerViewing(PlayerEntity player)
        {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler)
            {
                Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
                return inventory == MetalBarrelBlockEntity.this
                        || (inventory instanceof DoubleInventory doubleInventory && doubleInventory.isPart(MetalBarrelBlockEntity.this));
            }
            return false;
        }
    };

    public MetalBarrelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt))
        {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt))
        {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Override
    public int size()
    {
        return 27;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList()
    {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list)
    {
        this.inventory = list;
    }

    public NamedScreenHandlerFactory openHandler(PlayerEntity player, World world, BlockPos pos, BlockState state,
                                                 Inventory top, Inventory botttom)
    {
        // JAAAAAAANK
        return new NamedScreenHandlerFactory()
        {
            @Override
            public Text getDisplayName()
            {
                return getContainerName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
            {
                if (botttom != null && top != null)
                {
                    return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, new DoubleInventory(top, botttom));
                }
                else if (top != null)
                {
                    return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, top);
                }
                return null; // Invalid state, so return null.
            }
        };
    }

    @Override
    protected Text getContainerName()
    {
        return Text.translatable("container.barrel");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public void onOpen(PlayerEntity player)
    {
        if (!this.removed && !player.isSpectator())
        {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    public void onClose(PlayerEntity player)
    {
        if (!this.removed && !player.isSpectator())
        {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void tick()
    {
        if (!this.removed)
        {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    void setOpen(BlockState state, boolean open)
    {
        this.world.setBlockState(this.getPos(), state.with(BarrelBlock.OPEN, open), Block.NOTIFY_ALL);
    }

    void playSound(BlockState state, SoundEvent soundEvent)
    {
        Vec3i vec3i = state.get(BarrelBlock.FACING).getVector();
        double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }

    public Storage<ItemVariant> getStorage(@Nullable Direction direction)
    {
        BlockState state = getCachedState();
        @Nullable MetalBarrelBlockEntity other = MetalBarrelBlock.getOther(world, pos, state);

        if (other == null)
        {
            return InventoryStorage.of(this, direction);
        }
        else
        {
            return new CombinedSlottedStorage<>(List.of(
                    InventoryStorage.of(this, direction),
                    InventoryStorage.of(other, direction)
            ));
        }
    }
}
