package com.neep.neepmeat.entity.scutter;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.util.ItemUtil;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FarmingScutter extends ScutterEntity
{
    private final ImplementedInventory inventory = ImplementedInventory.ofSize(6);
    private final InventoryStorage storage = InventoryStorage.of(inventory, null);

    private final LinkedHashSet<BlockPos> targets = new LinkedHashSet<>(); // Preserve order

    @Nullable private BlockPos storagePos;
    private BlockPos homePos;

    public FarmingScutter(EntityType<? extends ScutterEntity> type, World world)
    {
        super(type, world);
        homePos = BlockPos.ofFloored(getPos()); // Just so that it's not null
    }

    public void setStoragePos(@Nullable BlockPos pos)
    {
        this.storagePos = pos;
    }

    public void setHomePos(BlockPos homePos)
    {
        this.homePos = homePos;
    }

    public @Nullable BlockPos getStoragePos()
    {
        return storagePos;
    }

    public BlockPos getHomePos()
    {
        return homePos;
    }

    public static boolean isGrownCrop(BlockState state)
    {
        return state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMature(state);
    }

    public static boolean isFarmland(BlockState state)
    {
        return state.isOf(Blocks.FARMLAND);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
        nbt.put("home", NbtHelper.fromBlockPos(homePos));
        if (storagePos != null)
            nbt.put("storage", NbtHelper.fromBlockPos(storagePos));
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
        this.homePos = NbtHelper.toBlockPos(nbt.getCompound("home"));
        if (nbt.contains("storage"))
            this.storagePos = NbtHelper.toBlockPos(nbt.getCompound("storage"));
    }

    @Override
    protected void initGoals()
    {
        super.initGoals();
        goalSelector.add(1, new ScutterBreakCropGoal(this));
        goalSelector.add(2, new ScutterMoveToCropGoal(this, 0.3f, 32));
        goalSelector.add(3, new ScutterFindGrownCropsGoal(this, 40));
        goalSelector.add(4, new ScutterReturnHomeGoal(this, 0.3f, 32));
//        goalSelector.add(4, new ScutterFindFarmlandGoal(this, 7, 1));
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand)
    {
        if (player.isSneaking())
        {
            if (!player.isCreative())
            {
                dropItem(NMItems.FARMING_SCUTTER);
            }

            remove(RemovalReason.DISCARDED);

            return ActionResult.SUCCESS;
        }
        return super.interactAt(player, hitPos, hand);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!getWorld().isClient()
                && getWorld().getTime() % 20 == 0
                && isAtHome()
                && !inventory.isEmpty())
        {
            depositItems();
        }

        targets.removeIf(p -> !isGrownCrop(getWorld().getBlockState(p)));
    }

    public void harvest(World world, BlockPos pos, BlockState state, CropBlock block)
    {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        List<ItemStack> stacks = Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, this, ItemStack.EMPTY);
        try (Transaction transaction = Transaction.openOuter())
        {
            for (var stack : stacks)
            {
                storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
            }
            transaction.commit();
        }
        world.setBlockState(pos, block.withAge(0));

        world.playSound(this, getBlockPos(), block.getSoundGroup(state).getBreakSound(), SoundCategory.BLOCKS, 1, 1);
    }

    public void depositItems()
    {
        if (storagePos != null)
        {
            Storage<ItemVariant> homeStorage = ItemStorage.SIDED.find(getWorld(), storagePos, Direction.UP);

            if (homeStorage != null)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    long transferred = StorageUtil.move(storage, homeStorage, v -> true, Long.MAX_VALUE, transaction);
                    if (transferred > 0)
                    {
                        getWorld().playSound(this, getBlockPos(), NMSounds.DEPOSIT_ITEMS, SoundCategory.NEUTRAL, 1, 1);
                        transaction.commit();
                    }
                }

            }
        }

        for (var stack : inventory.getItems())
        {
            dropStack(stack);
        }
    }

    public void addTargets(Collection<BlockPos> nearest)
    {
        targets.addAll(nearest);
    }

    public Set<BlockPos> getTargets()
    {
        return targets;
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack()
    {
        return new ItemStack(NMItems.FARMING_SCUTTER);
    }

    public ImplementedInventory getInventory()
    {
        return inventory;
    }

    public boolean needsEmptying()
    {
        return inventory.emptyStacks() == 0;
    }

    public boolean isAtHome()
    {
        return getPos().isInRange(getHomePos().toCenterPos(), 1.1);
    }
}
