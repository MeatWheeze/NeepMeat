package com.neep.neepmeat.machine.hydraulic_press;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.RecipeBehaviour;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.casting_basin.CastingBasinBlockEntity;
import com.neep.neepmeat.machine.casting_basin.CastingBasinStorage;
import com.neep.neepmeat.recipe.AbstractPressingRecipe;
import com.neep.neepmeat.recipe.MobSqueezingRecipe;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class HydraulicPressBlockEntity extends SyncableBlockEntity
{
    public static final long EXTEND_AMOUNT = FluidConstants.BUCKET;
    public static final int TICKS_EXTENDED = 30;
    
    protected boolean recipeControlled = true;
    protected short recipeState;
    protected int extensionTicks;

    protected AbstractPressingRecipe<CastingBasinStorage> currentRecipe;
    protected Identifier recipeId;
    protected SqueezingRecipeBehaviour behaviour = new SqueezingRecipeBehaviour();

    public float renderExtension;

    public WritableSingleFluidStorage fluidStorage = new WritableSingleFluidStorage(EXTEND_AMOUNT, this::sync)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return super.canInsert(variant) && variant.isOf(Fluids.WATER) && (!recipeControlled || (recipeState == 0 && hasRecipe()));
        }

        @Override
        protected boolean canExtract(FluidVariant variant)
        {
            return super.canExtract(variant) && variant.isOf(Fluids.WATER) && (!recipeControlled || recipeState == 2);
        }
    };

    public HydraulicPressBlockEntity(BlockEntityType<HydraulicPressBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public HydraulicPressBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.HYDRAULIC_PRESS, pos, state);
    }

    public void setState(int state)
    {
        this.recipeState = (short) state;
    }

    protected boolean hasRecipe()
    {
        loadRecipe();
        behaviour.load(world);
        return currentRecipe != null || behaviour.getCurrentRecipe() != null;
    }

    protected void startPressRecipe(CastingBasinStorage storage, AbstractPressingRecipe<CastingBasinStorage> recipe)
    {
        if (recipe == null || !storage.item(null).isEmpty())
            return;

        storage.lock();
        this.currentRecipe = recipe;
        this.recipeState = 0;
    }

    protected void finishPressRecipe(CastingBasinStorage storage)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            FluidVariant variant = currentRecipe.takeInputs(storage, transaction);
            if (variant != null)
            {
                transaction.commit();
            }
            else
            {
                transaction.abort();
            }
        }
    }

    protected void finishRecipe()
    {
        if (world.getBlockEntity(pos.down()) instanceof CastingBasinBlockEntity basin)
        {
            finishPressRecipe(basin.getStorage());
        }
        else behaviour.finishRecipe();
    }

    protected void stopPressRecipe(@Nullable CastingBasinStorage storage)
    {
        if (storage != null) storage.unlock();

        this.recipeState = 0;
        this.recipeId = null;
        this.currentRecipe = null;
    }

    protected void stopRecipe()
    {
        CastingBasinStorage storage = world.getBlockEntity(pos.down()) instanceof CastingBasinBlockEntity be ? be.getStorage() : null;
        stopPressRecipe(storage);
        behaviour.interrupt();
    }

    // I'll clean this up one day. I promise!
    public void tick()
    {
        if (world.getBlockEntity(pos.down()) instanceof CastingBasinBlockEntity basin)
        {
            loadRecipe();

            if (currentRecipe == null)
            {
                AbstractPressingRecipe<CastingBasinStorage> recipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.FAT_PRESSING, basin.getStorage(), world).orElse(null);

                if (recipe == null)
                    recipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.PRESSING, basin.getStorage(), world).orElse(null);

                startPressRecipe(basin.getStorage(), recipe);

            }
        }
        else if (world.isAir(pos.down()) && behaviour.getCurrentRecipe() == null)
        {
            MobSqueezingRecipe squeezingRecipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.MOB_SQUEEZING, new MobSqueezeContext(world, pos), world).orElse(null);
            behaviour.startRecipe(squeezingRecipe);
        }

        if (currentRecipe != null && !(world.getBlockEntity(pos.down()) instanceof CastingBasinBlockEntity)
        || behaviour.getCurrentRecipe() != null && !behaviour.getCurrentRecipe().matches(new MobSqueezeContext(world, pos), world))
        {
            stopRecipe();
            setState(2);
        }

        tickExtension();
    }

    private void tickExtension()
    {
        if (currentRecipe == null && behaviour.getCurrentRecipe() == null)
            return;

        if (recipeState == 0 && fluidStorage.getAmount() >= EXTEND_AMOUNT)
        {
            this.extensionTicks = 0;
            this.recipeState = 1;
        }
        if (recipeState == 1)
        {
            extensionTicks = Math.min(TICKS_EXTENDED, extensionTicks + 1);
            if (extensionTicks >= TICKS_EXTENDED)
            {
                this.recipeState = 2;
                finishRecipe();
            }
        }
        if (recipeState == 2 && fluidStorage.getAmount() == 0)
        {
            stopRecipe();
        }
    }

    public WritableSingleFluidStorage getStorage(Direction direction)
    {
        Direction facing = getCachedState().get(HydraulicPressBlock.FACING);
        return direction == null || direction == facing || direction == facing.getOpposite() ? fluidStorage : null;
    }

    public void loadRecipe()
    {
        if (currentRecipe == null)
        {
            if (recipeId != null)
                currentRecipe = (AbstractPressingRecipe<CastingBasinStorage>) world.getRecipeManager().get(recipeId).orElse(null);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        fluidStorage.writeNbt1(nbt);
        nbt.putShort("recipeState", recipeState);
        nbt.putBoolean("recipeControlled", recipeControlled);
        if (currentRecipe != null)
        {
            nbt.putString("currentRecipe", currentRecipe.getId().toString());
        }

        behaviour.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fluidStorage.readNbt(nbt);
        this.recipeState = nbt.getShort("recipeState");
        this.recipeControlled = nbt.getBoolean("recipeControlled");
        String recipeString = nbt.getString("currentRecipe");
        if (recipeString != null)
        {
            this.recipeId = new Identifier(recipeString);
        }
        else this.recipeId = null;

        behaviour.readNbt(nbt);
    }

    public class SqueezingRecipeBehaviour extends RecipeBehaviour<MobSqueezingRecipe> implements NbtSerialisable
    {
        @Override
        public void startRecipe(MobSqueezingRecipe recipe)
        {
            if (recipe == null)
                return;

            setRecipe(recipe);
            recipeState = 0;
        }

        @Override
        public void interrupt()
        {
            recipeState = 0;
            this.recipeId = null;
            this.currentRecipe = null;
        }

        @Override
        public void finishRecipe()
        {
            load(world);
            getCurrentRecipe().finishRecipe(new MobSqueezeContext(world, pos), world);
        }
    }

    public enum Mode
    {
        PRESSING,
        FAT_PRESSING,
        SQUEEZING;
    }
}
