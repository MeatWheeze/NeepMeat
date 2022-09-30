package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.ingredient.FluidIngredient;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class MixerBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    protected MixerStorage storage = new MixerStorage(this);
    protected MixingRecipe currentRecipe;
    protected Identifier currentRecipeId;
    protected int processLength;
    protected float progress;
    protected int cooldownTicks;

    public static float INCREMENT_MAX = 2;
    public static float INCREMENT_MIN = 0.1f;
    protected float progressIncrement;
    protected long processStart;

    public float bladeAngle;
    public float bladeSpeed;

    public MixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MixerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MIXER, pos, state);
    }

    public Storage<FluidVariant> getFluidStorage(@Nullable Direction direction)
    {
        return getOutputStorage();
    }

    public WritableStackStorage getItemStorage(@Nullable Direction direction)
    {
        return storage.getItemInput();
    }

    public void setCurrentRecipe(@Nullable MixingRecipe recipe)
    {
        this.currentRecipe = recipe;
        this.currentRecipeId = recipe != null ? recipe.id : null;
        this.cooldownTicks = 10;
    }

    public MixingRecipe getCurrentRecipe()
    {
        return currentRecipe;
    }

    public List<Storage<FluidVariant>> getAdjacentStorages()
    {
        if (getWorld().isClient())
            return null;

        List<Storage<FluidVariant>> out = new LinkedList<>();
        for (Direction direction : Direction.values())
        {
            if (direction == Direction.DOWN || direction == Direction.UP)
                continue;

            BlockPos offset = getPos().offset(direction);
            BlockState state = getWorld().getBlockState(offset);
            BlockEntity be = getWorld().getBlockEntity(offset);
            Storage<FluidVariant> storage;
            if ((storage = FluidStorage.SIDED.find(getWorld(), pos, state, be, direction.getOpposite())) != null)
            {
                out.add(storage);
            }
        }
        return out;
    }

    public Storage<FluidVariant> getOutputStorage()
    {
        return storage.getFluidOutput();
    }

    public void startDutyCycle()
    {
        if (currentRecipe == null && getOutputStorage() != null)
        {
            MixingRecipe recipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.MIXING, storage, world).orElse(null);

            if (recipe != null && getOutputStorage().simulateInsert(recipe.fluidOutput.resource(),
                        recipe.fluidOutput.amount(), null) == recipe.fluidOutput.amount())
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
                    }
                    else
                        transaction.abort();
                }
            }
        }
        sync();
    }

    public void endDutyCycle()
    {
        if (currentRecipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (getCurrentRecipe().ejectOutput(storage, transaction))
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
        nbt.putFloat("increment", progressIncrement);
        nbt.putInt("process_time", processLength);
        nbt.putLong("process_start", processStart);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.currentRecipeId = new Identifier(nbt.getString("current_recipe"));
        readCurrentRecipe();

        this.progress = nbt.getFloat("progress");
        this.progressIncrement = nbt.getFloat("increment");
        this.processLength = nbt.getInt("process_time");
        this.processStart = nbt.getLong("process_start");
        storage.readNbt(nbt);
    }

    public void readCurrentRecipe()
    {
        if (world != null)
        {
            Optional<? extends Recipe<?>> optional = Objects.requireNonNull(getWorld()).getRecipeManager().get(currentRecipeId);
            optional.ifPresentOrElse(recipe -> this.currentRecipe = (MixingRecipe) recipe,
                    () -> this.currentRecipe = null);
        }
    }

    public void tick()
    {
        readCurrentRecipe();
        if (currentRecipe != null && progressIncrement > INCREMENT_MIN)
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

        if (currentRecipe != null && progressIncrement > INCREMENT_MIN)
        {
            spawnMixingParticles(currentRecipe.fluidInput1, 2, 0.2, 0.5);
            spawnMixingParticles(currentRecipe.fluidInput2, 2, 0.2, 0.5);
        }
//        sync();
    }

    public void dropItems()
    {
        Transaction transaction = Transaction.openOuter();
        Iterator<StorageView<ItemVariant>> it = this.storage.itemInput.iterator(transaction);
        while (it.hasNext())
        {
            StorageView<ItemVariant> view = it.next();
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, view.getResource().toStack((int) view.getAmount()));
        }
        transaction.commit();
    }

    public void spawnMixingParticles(FluidIngredient ingredient, int count, double dy, double speed)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(new SwirlingParticleEffect(NMParticles.BLOCK_SWIRL,
                    ingredient.resource().getObject().getDefaultState().getBlockState(), 0.4, speed), pos.getX() + 0.5, pos.getY() + 0.5 + 1, pos.getZ() + 0.5, count, 0, dy, 0, 0.1);
        }
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        tick();
        return true;
    }

    @Override
    public void setWorkMultiplier(float multiplier)
    {
        this.progressIncrement = MathHelper.lerp(multiplier, INCREMENT_MIN, INCREMENT_MAX);
    }
}
