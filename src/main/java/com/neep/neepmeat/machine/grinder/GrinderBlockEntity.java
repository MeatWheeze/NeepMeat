package com.neep.neepmeat.machine.grinder;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.block.pipe.IItemPipe;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.GrindingRecipe;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class GrinderBlockEntity extends SyncableBlockEntity
{
    protected GrinderStorage storage = new GrinderStorage(this);
    protected int progress;
    protected int cooldownTicks = 2;
    protected int processLength;
    protected Identifier currentRecipeId;
    protected GrindingRecipe currentRecipe;

    public GrinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public GrinderBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.GRINDER, pos, state);
    }

    public GrindingRecipe getCurrentRecipe()
    {
        return currentRecipe;
    }

    public void setCurrentRecipe(@Nullable GrindingRecipe recipe)
    {
        this.currentRecipe = recipe;
        this.currentRecipeId = recipe != null ? recipe.getId() : null;
    }

    public void readCurrentRecipe()
    {
        if (world != null)
        {
            Optional<? extends Recipe<?>> optional = getWorld().getRecipeManager().get(currentRecipeId);
            optional.ifPresentOrElse(recipe -> this.currentRecipe = (GrindingRecipe) recipe,
                    () -> this.currentRecipe = null);
        }
    }

    public GrinderStorage getStorage()
    {
        return storage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putInt("progress", progress);
        nbt.putInt("process_length", processLength);

        if (currentRecipe != null)
            nbt.putString("current_recipe", currentRecipe.getId().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.progress = nbt.getInt("progress");
        this.processLength = nbt.getInt("process_length");
        this.currentRecipeId = new Identifier(nbt.getString("current_recipe"));
        readCurrentRecipe();
    }

    public void tick()
    {
        readCurrentRecipe();
        if (!storage.getOutputStorage().isEmpty())
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ejectOutput(transaction);
            }
        }

        if (currentRecipe != null)
        {
            ++progress;
            if (progress >= this.processLength)
            {
                endDutyCycle();
                this.progress = 0;
            }
        }
        else
        {
            ++progress;
            if (progress >= this.cooldownTicks)
            {
                startDutyCycle();
                this.progress = 0;
            }
        }
    }

    private void startDutyCycle()
    {
        if (currentRecipe == null && storage.outputStorage.isEmpty() && !storage.inputStorage.isEmpty())
        {
            GrindingRecipe recipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.GRINDING, storage, world).orElse(null);

            if (recipe != null)
            {
                long ins = storage.outputStorage.simulateInsert(recipe.getItemInput().resource(), recipe.getItemInput().amount(), null);
            }

            if (recipe != null && storage.outputStorage.simulateInsert(recipe.getItemOutput().resource(),
                    recipe.getItemOutput().amount(), null) == recipe.getItemOutput().amount())
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    if (recipe.takeInputs(storage, transaction))
                    {
                        transaction.commit();
                        setCurrentRecipe(recipe);
                        this.processLength = recipe.getTime();
                    }
                    else
                        transaction.abort();
                }
            }
        }
        sync();
    }

    private void endDutyCycle()
    {
        if (currentRecipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (getCurrentRecipe().ejectOutput(storage, transaction))
                {
                    ejectOutput(transaction);
                    transaction.commit();
                }
                else
                    transaction.abort();
            }
            this.setCurrentRecipe(null);
        }
        sync();
    }

    protected void ejectOutput(TransactionContext transaction)
    {
        Direction facing = getCachedState().get(GrinderBlock.FACING);
        ItemStack stack = storage.outputStorage.getAsStack();

        BlockPos offsetPos = pos.offset(facing);
        BlockState offsetState = world.getBlockState(offsetPos);
        Storage<ItemVariant> ejectStorage;
        if (offsetState.getBlock() instanceof IItemPipe pipe && pipe.getConnections(offsetState, d -> true).contains(facing.getOpposite()))
        {
            pipe.insert(world, offsetPos, offsetState, facing.getOpposite(), new ItemInPipe(stack, world.getTime()));
        }
        else if ((ejectStorage = ItemStorage.SIDED.find(world, offsetPos, Direction.UP)) != null)
        {
            ejectStorage.insert(storage.outputStorage.getResource(), stack.getCount(), transaction);
        }
        else
        {
            Vec3d itemPos = Vec3d.ofCenter(getPos(), 0.5).add(facing.getOffsetX(), facing.getOffsetY(), facing.getOffsetZ());
            ItemEntity entity = new ItemEntity(getWorld(), itemPos.x, itemPos.y, itemPos.z, storage.outputStorage.getAsStack());
            float mult = 0.1f;
            entity.setVelocity(facing.getOffsetX() * mult, facing.getOffsetY() * mult, facing.getOffsetZ() * mult);
            world.spawnEntity(entity);
        }
        storage.outputStorage.extract(storage.outputStorage.getResource(), stack.getCount(), transaction);
        Vec3d xpPos = Vec3d.ofCenter(pos, 0.5).add(facing.getOffsetX() * 0.6, facing.getOffsetY() * 0.6, facing.getOffsetZ() * 0.6);
        ExperienceOrbEntity.spawn((ServerWorld) world, xpPos, (int) Math.ceil(storage.getXpStorage().getAmount()));
        storage.xpStorage.extract(Float.MAX_VALUE, transaction);
    }

    public static void serverTick(World world, BlockPos pos,BlockState state, GrinderBlockEntity be)
    {
        be.tick();
    }
}
