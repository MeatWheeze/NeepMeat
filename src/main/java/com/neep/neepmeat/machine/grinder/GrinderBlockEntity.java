package com.neep.neepmeat.machine.grinder;

import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.util.MeatStorageUtil;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.block.entity.MotorisedMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.recipe.GrindingRecipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class GrinderBlockEntity extends MotorisedMachineBlockEntity
{
    protected Random jrandom = new Random();
    protected GrinderStorage storage = new GrinderStorage(this);
    protected int cooldownTicks = 2;
    protected int processLength;

    protected float progress;

    protected Identifier currentRecipeId;
    protected GrindingRecipe lastRecipe;
    protected GrindingRecipe currentRecipe;

    public GrinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 0.02f, 0.02f, 2);
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
            Optional<? extends MeatRecipe<?>> optional = MeatRecipeManager.getInstance().get(currentRecipeId);
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
        nbt.putFloat("power", power);
        nbt.putFloat("progress", progress);
        nbt.putInt("process_length", processLength);
        nbt.putFloat("progress_increment", progressIncrement);

        if (currentRecipe != null)
            nbt.putString("current_recipe", currentRecipe.getId().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.power = nbt.getFloat("power");
        this.progress = nbt.getFloat("progress");
        this.processLength = nbt.getInt("process_length");
        this.currentRecipeId = new Identifier(nbt.getString("current_recipe"));
        this.progressIncrement = nbt.getFloat("progress_increment");
        readCurrentRecipe();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        world.updateComparators(pos, getCachedState().getBlock());
    }

    public void tick()
    {
        readCurrentRecipe();

//        if (progressIncrement == 0)
//        {
//            currentRecipe = null;
//            currentRecipeId = null;
//            progress = 0;
//            return;
//        }

        // Eject outputs
        if (!storage.getOutputStorage().isEmpty() || !storage.extraStorage.isEmpty())
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ejectOutput(transaction);
                transaction.commit();
            }
        }

        if (currentRecipe != null)
        {
            progress = Math.min(processLength, progress + progressIncrement);

            if (progress >= this.processLength || !getCurrentRecipe().matches(storage))
            {
                endRecipe();
                this.progress = 0;
            }
        }
        else
        {
            ++progress;

            if (progress >= this.cooldownTicks)
            {
                startRecipe();
                this.progress = 0;
            }
        }

        super.serverTick();
    }

    private void startRecipe()
    {
        if (currentRecipe == null && storage.outputStorage.isEmpty() && !storage.inputStorage.isEmpty())
        {
            GrindingRecipe recipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.GRINDING, storage).orElse(null);

            if (recipe != null && MeatStorageUtil.simulateInsert(storage.outputStorage, ItemVariant.of(recipe.getItemOutput().resource()),
                                                                 recipe.getItemOutput().amount(), null) == recipe.getItemOutput().amount())
            {
                setCurrentRecipe(recipe);
                this.processLength = recipe.getTime();
                sync();
            }
        }
    }

    private void endRecipe()
    {
        if (currentRecipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (getCurrentRecipe().matches(storage) && getCurrentRecipe().takeInputs(storage, transaction) && getCurrentRecipe().ejectOutputs(storage, transaction))
                {
                    ejectOutput(transaction);
                    transaction.commit();
                }
                else transaction.abort();
            }
            this.setCurrentRecipe(null);
        }
        sync();
    }

    protected void ejectOutput(TransactionContext transaction)
    {
        Direction facing = getCachedState().get(GrinderBlock.FACING);
        CombinedStorage<ItemVariant, WritableStackStorage> combined = new CombinedStorage<>(List.of(storage.outputStorage, storage.extraStorage));
        ItemPipeUtil.storageToAny((ServerWorld) getWorld(), combined, pos, facing, transaction);
    }

    protected void ejectXP()
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            Direction facing = getCachedState().get(GrinderBlock.FACING);
            Vec3d xpPos = Vec3d.ofCenter(pos, 0.5).add(facing.getOffsetX() * 0.6, facing.getOffsetY() * 0.6, facing.getOffsetZ() * 0.6);
            ExperienceOrbEntity.spawn((ServerWorld) world, xpPos, (int) Math.ceil(storage.getXpStorage().getAmount()));
            storage.xpStorage.extract(Float.MAX_VALUE, transaction);
            transaction.commit();
        }
    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        tick();
        return currentRecipe != null;
    }

    @Override
    public float getLoadTorque()
    {
        return 400f;
    }

    public void clientTick()
    {
        float intensity = progressIncrement / maxIncrement;

        // Particles will be more frequent at higher power. Clamp above 1 to prevent / 0.
        int tickInterval = (int) MathHelper.clamp(1, 1 / (intensity * 2), 100);

        if ((world.getTime() % tickInterval) == 0
                && currentRecipe != null
                && !storage.inputStorage.isEmpty()
                && progressIncrement > 0)
        {
            double px = getPos().getX() + 0.5 + (jrandom.nextFloat() - 0.5) * 0.5;
            double py = getPos().getY() + 0.8 + (jrandom.nextFloat() - 0.5) * 0.5;
            double pz = getPos().getZ() + 0.5 + (jrandom.nextFloat() - 0.5) * 0.5;

            double vx = (jrandom.nextFloat() - 0.5) * 0.2;
            double vy = jrandom.nextFloat() * Math.max(0.3, 0.5 * intensity);
            double vz = (jrandom.nextFloat() - 0.5) * 0.2;

            world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, storage.inputStorage.getAsStack()),
                px, py, pz, vx, vy, vz);
        }
    }
}
