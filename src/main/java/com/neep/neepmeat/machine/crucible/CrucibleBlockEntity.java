package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.OreFatRenderingRecipe;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class CrucibleBlockEntity extends SyncableBlockEntity
{
    protected CrucibleStorage storage;

    public CrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new CrucibleStorage(this);
    }

    public CrucibleBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.CRUCIBLE, pos, state);
    }

    public CrucibleStorage getStorage()
    {
        return storage;
    }

    public CombinedStorage<FluidVariant, SingleVariantStorage<FluidVariant>> getOutput()
    {
        List<SingleVariantStorage<FluidVariant>> storages = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            if (direction.getAxis().equals(Direction.Axis.Y))
                continue;

            BlockPos offset = pos.offset(direction);
            if (world.getBlockEntity(offset) instanceof AlembicBlockEntity be && be.getCachedState().get(AlembicBlock.FACING) == direction)
            {
                storages.add(be.getStorage(null));
            }
        }
        return new CombinedStorage<>(storages);
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

    public static int timeToFluid(int fuelTime)
    {
        return fuelTime * 16;
    }

    public void receiveItemEntity(ItemEntity entity)
    {
        ItemStack stack = entity.getStack();
        ItemVariant variant = ItemVariant.of(stack);
        try (Transaction transaction = Transaction.openOuter())
        {
            long decrement = storage.itemStorage.insert(variant, stack.getCount(), transaction);
            stack.decrement((int) decrement);
            transaction.commit();
        }
    }

    public long processItem(ItemVariant fuelVariant, long maxAmount, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            OreFatRenderingRecipe recipe = getWorld().getRecipeManager().getFirstMatch(NMrecipeTypes.ORE_FAT_RENDERING, storage, getWorld()).orElse(null);
            Item processItem;
            if (recipe != null && (processItem = recipe.takeInputs(storage, (int) maxAmount, inner)) != null)
            {
                inner.commit();
                spawnParticles((ServerWorld) world, pos, processItem, 20);
                return maxAmount;
            }
            inner.abort();
        }

        return 0;
    }

    public static void spawnParticles(ServerWorld world, BlockPos pos, Item item, int amount)
    {
        ItemStack stack = new ItemStack(item);
        Random random = new Random();
        for (int p = 0; p < amount; ++p)
        {
            double i = random.nextGaussian(0.5, 0.2);
            double j = random.nextFloat();
            double k = random.nextGaussian(0.5, 0.2);
            world.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), pos.getX() + i, pos.getY() + 1, pos.getZ() + k, 1, 0, 0.2, 0, 0.02);
        }
        for (int p = 0; p < amount; ++p)
        {
            double i = random.nextGaussian(0.5, 0.2);
            double j = random.nextFloat();
            double k = random.nextGaussian(0.5, 0.2);
            world.spawnParticles(ParticleTypes.SPLASH, pos.getX() + i, pos.getY() + 1, pos.getZ() + k, 1, 0, 0.2, 0, 0.02);
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.HOSTILE, 1f, 0.8f);
    }
}
