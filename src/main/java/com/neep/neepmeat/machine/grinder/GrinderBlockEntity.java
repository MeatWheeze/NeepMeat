package com.neep.neepmeat.machine.grinder;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.util.MeatStorageUtil;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
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

@SuppressWarnings("UnstableApiUsage")
public class GrinderBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    protected GrinderStorage storage = new GrinderStorage(this);
    protected int cooldownTicks = 2;
    protected int processLength;

    public static final float INCREMENT_MAX = 2;
    public static final float INCREMENT_MIN = 0.2f;
    public static final float MULTIPLIER_MIN = 0.05f;
    protected float progressIncrement;
    protected float progress;

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
        nbt.putFloat("progress", progress);
        nbt.putInt("process_length", processLength);

        if (currentRecipe != null)
            nbt.putString("current_recipe", currentRecipe.getId().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.progress = nbt.getFloat("progress");
        this.processLength = nbt.getInt("process_length");
        this.currentRecipeId = new Identifier(nbt.getString("current_recipe"));
        readCurrentRecipe();
    }

    public void tick()
    {
        readCurrentRecipe();

        if (progressIncrement == 0)
        {
            currentRecipe = null;
            return;
        }

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

            ((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, getCurrentRecipe().getItemOutput().resource().getDefaultStack()),
                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 1, 0.1, 0, 0.1, 0.01);

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
    }

    private void startRecipe()
    {
        if (currentRecipe == null && storage.outputStorage.isEmpty() && !storage.inputStorage.isEmpty())
        {
            GrindingRecipe recipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.GRINDING, storage).orElse(null);

            if (recipe != null && MeatStorageUtil.simulateInsert(storage.outputStorage, ItemVariant.of(recipe.getItemOutput().resource()),
                                                                 recipe.getItemOutput().amount(), null) == recipe.getItemOutput().amount())
            {
//                try (Transaction transaction = Transaction.openOuter())
//                {
//                    if (recipe.takeInputs(storage, transaction))
//                    {
//                        transaction.commit();
                setCurrentRecipe(recipe);
                this.processLength = recipe.getTime();
//                    }
//                    else
//                        transaction.abort();
//                }
            }
        }
        sync();
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
    public boolean tick(IMotorBlockEntity motor)
    {
        tick();
        return currentRecipe != null;
    }

    @Override
    public void setInputPower(float power)
    {
        this.progressIncrement = MathHelper.lerp(power, 0, INCREMENT_MAX);
        if (power < MULTIPLIER_MIN) progressIncrement = 0;
    }

    @Override
    public float getLoadTorque()
    {
        return 400f;
    }
}
