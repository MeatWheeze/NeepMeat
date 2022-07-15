package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class MixerBlockEntity extends SyncableBlockEntity
{
    protected MixerStorage storage = new MixerStorage(this);
    protected MixingRecipe currentRecipe;
    protected long processStart;
    protected int processTime;

    public MixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MixerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MIXER, pos, state);
    }

    public Storage<FluidVariant> getFluidStorage(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return null;
    }

    public void setCurrentRecipe(MixingRecipe recipe)
    {
        this.currentRecipe = recipe;
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

            BlockPos offset = getPos().up().offset(direction);
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
        BlockPos offset = getPos().down();
        Storage<FluidVariant> storage;
        if ((storage = FluidStorage.SIDED.find(getWorld(), offset, Direction.UP)) != null)
        {
            return storage;
        }
        return null;
    }

    public void startDutyCycle()
    {
        if (currentRecipe == null && getOutputStorage() != null)
        {
            MixingRecipe recipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.MIXING_TYPE, storage, world).orElse(null);
            if (recipe != null)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    if (recipe.takeInputs(storage, transaction))
                    {
                        transaction.commit();
                        setCurrentRecipe(recipe);
                        this.processTime = recipe.processTime;
                        this.processStart = world.getTime();
                        world.createAndScheduleBlockTick(pos, getCachedState().getBlock(), processTime);
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
            this.currentRecipe = null;
        }
        sync();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
//        storage.writeNbt(nbt);
        if (currentRecipe != null)
            nbt.putString("current_recipe", currentRecipe.getId().toString());

        nbt.putInt("process_time", processTime);
        nbt.putLong("process_start", processStart);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
//        storage.readNbt(nbt);
        Optional<? extends Recipe<?>> optional = getWorld().getRecipeManager().get(new Identifier(nbt.getString("current_recipe")));
        optional.ifPresentOrElse(recipe -> this.currentRecipe = (MixingRecipe) recipe,
                () -> this.currentRecipe = null);

        this.processTime = nbt.getInt("process_time");
        this.processStart = nbt.getLong("process_start");
    }
}
