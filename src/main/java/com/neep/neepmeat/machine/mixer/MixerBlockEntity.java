package com.neep.neepmeat.machine.mixer;

import com.google.common.base.Suppliers;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.block.entity.MotorisedMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.fluid.CauldronStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class MixerBlockEntity extends MotorisedMachineBlockEntity
{
    protected final MixerStorage storage = new MixerStorage(this);

    @Nullable protected MixingRecipe currentRecipe;
    protected int processLength;
    protected float progress;
    protected int cooldownTicks = 2;

    protected long processStart;

    public float bladeAngle;
    public float bladeSpeed;

    private final Supplier<List<RetrievalTarget<FluidVariant>>> storageCaches = Suppliers.memoize(() ->
    {
        ObjectArrayList<RetrievalTarget<FluidVariant>> list = new ObjectArrayList<>();
        for (Direction direction : Direction.values())
        {
            if (direction.getAxis().isVertical())
                continue;

            list.add(RetrievalTarget.of(FluidStorage.SIDED, (ServerWorld) getWorld(), getPos(), direction.getOpposite()));
        }
        return new ObjectImmutableList<>(list);
    });

    // Set to null at the end of every tick.
    @Nullable private List<Storage<FluidVariant>> adjacentStorageCache;

    public MixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 0.04f, 0.05f, 2);
    }

    public MixerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MIXER, pos, state);
    }

    public Storage<FluidVariant> getFluidStorage(@Nullable Direction direction)
    {
        return getExtractOutput();
    }

    public static WritableStackStorage getItemStorageFromTop(World world, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction)
    {
        if (world.getBlockEntity(pos.down()) instanceof MixerBlockEntity mixer)
        {
            return mixer.getItemStorage(Direction.UP);
        }
        return null;
    }

    public static Storage<FluidVariant> getFluidStorageFromTop(World world, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction)
    {
        if (world.getBlockEntity(pos.down()) instanceof MixerBlockEntity mixer)
        {
            return mixer.getExtractOutput();
        }
        return null;
    }

    public WritableStackStorage getItemStorage(@Nullable Direction direction)
    {
        return storage.getItemInput();
    }

    public void setCurrentRecipe(@Nullable MixingRecipe recipe)
    {
        this.currentRecipe = recipe;
//        this.currentRecipeId = recipe != null ? recipe.id : null;
        this.cooldownTicks = 2;
    }

    public MixingRecipe getCurrentRecipe()
    {
        return currentRecipe;
    }

    public List<Storage<FluidVariant>> getAdjacentStorages()
    {
        // Build the cache from the other cache
        if (adjacentStorageCache == null)
        {
            adjacentStorageCache = new ObjectArrayList<>();
            for (var cache : storageCaches.get())
            {
                Storage<FluidVariant> storage = cache.find();
                if (storage != null)
                {
                    // Cauldron storages hate the mixer, so we ignore them. Easy peasy.
                    if (storage instanceof CauldronStorage)
                        continue;

                    adjacentStorageCache.add(storage);
                }
            }
        }

        return adjacentStorageCache;
    }

    public Storage<FluidVariant> getOutputStorage()
    {
        return storage.getFluidOutput();
    }

    public Storage<FluidVariant> getExtractOutput()
    {
        return storage.getExtractOutput();
    }

    public void startDutyCycle()
    {
        if (currentRecipe == null)
        {
            MixingRecipe recipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.MIXING, storage).orElse(null);

            if (recipe != null && MeatlibStorageUtil.simulateInsert(getOutputStorage(), FluidVariant.of(recipe.fluidOutput.resource()),
                                                                 recipe.fluidOutput.maxAmount(), null) == recipe.fluidOutput.maxAmount())
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    if (recipe.takeInputs(storage, transaction))
                    {
                        transaction.commit();
                        setCurrentRecipe(recipe);
                        this.processLength = recipe.processTime;
                        this.processStart = world.getTime();
//                        world.createAndScheduleBlockTick(pos, getCachedState().getBlock(), processTime);
                        sync();
                    }
                    else
                        transaction.abort();
                }
            }
        }
    }

    public void endDutyCycle()
    {
        if (currentRecipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (getCurrentRecipe().ejectOutputs(storage, transaction))
                    transaction.commit();
                else
                    transaction.abort();
            }
            this.setCurrentRecipe(null);
        }
        sync();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
//        storage.writeNbt(nbt);
        if (currentRecipe != null)
            nbt.putString("current_recipe", currentRecipe.getId().toString());

        nbt.putFloat("progress", progress);
        nbt.putInt("process_time", processLength);
        nbt.putLong("process_start", processStart);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.currentRecipe = MiscUtil.ifPresentOrNull(nbt, "current_recipe",
                s -> (MixingRecipe) MeatlibRecipes.getInstance().get(Identifier.tryParse(s)).orElse(null));

        this.progress = nbt.getFloat("progress");
        this.processLength = nbt.getInt("process_time");
        this.processStart = nbt.getLong("process_start");
        storage.readNbt(nbt);
    }

//    public void readCurrentRecipe()
//    {
//        if (world != null)
//        {
//            Optional<? extends Recipe<?>> optional = Objects.requireNonNull(getWorld()).getRecipeManager().get(currentRecipeId);
//            optional.ifPresentOrElse(recipe -> this.currentRecipe = (MixingRecipe) recipe,
//                    () -> this.currentRecipe = null);
//        }
//    }

    public void tick()
    {
        if (currentRecipe != null && progressIncrement > minIncrement)
        {
            progress = Math.min(processLength, progress + progressIncrement);
            if (progress >= this.processLength)
            {
                endDutyCycle();
                this.progress = 0;
            }
            sync();
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

        if (currentRecipe != null && progressIncrement > minIncrement)
        {
//            spawnMixingParticles(storage.displayInput1, 2, 0.2, 0.5);
//            spawnMixingParticles(storage.displayInput2, 2, 0.2, 0.5);
        }
//        sync();

        adjacentStorageCache = null;
    }

    public void dropItems()
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            for (StorageView<ItemVariant> view : this.storage.itemInput)
            {
                ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, view.getResource().toStack((int) view.getAmount()));
            }
            transaction.commit();
        }
    }

    public void spawnMixingParticles(FluidVariant ingredient, int count, double dy, double speed)
    {
        if (!ingredient.isBlank() && getWorld() instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(new SwirlingParticleEffect(NMParticles.BLOCK_SWIRL,
                    ingredient.getObject().getDefaultState().getBlockState(), 0.4, speed), pos.getX() + 0.5, pos.getY() + 0.5 + 1, pos.getZ() + 0.5, count, 0, dy, 0, 0.1);
        }
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
        tick();
        return true;
    }
}
