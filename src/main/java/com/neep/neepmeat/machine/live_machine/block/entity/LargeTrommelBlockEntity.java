package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.component.PoweredComponent;
import com.neep.neepmeat.machine.small_trommel.TrommelStorage;
import com.neep.neepmeat.recipe.TrommelRecipe;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LargeTrommelBlockEntity extends SyncableBlockEntity implements LivingMachineComponent, PoweredComponent
{
    private float progressIncrement;
    private final InputSlot inputSlot = new InputSlot(9000, this::sync);

    public LargeTrommelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return isRemoved();
    }

    public InputSlot getStorage()
    {
        return inputSlot;
    }

    @Override
    public ComponentType<? extends LivingMachineComponent> getComponentType()
    {
        return LivingMachineComponents.LARGE_TROMMEL;
    }

    @Override
    public float progressIncrement()
    {
        return progressIncrement;
    }

    @Override
    public void setProgressIncrement(float progressIncrement)
    {
        this.progressIncrement = progressIncrement;
    }

    public static class InputSlot extends WritableSingleFluidStorage implements StorageView<FluidVariant>
    {
        @Nullable private TrommelRecipe recipe;
        private float progress;
        private final float totalProgress = 30; // TODO: Hardcoded for now, the same as the small trommel.

        public InputSlot(long capacity, Runnable finalCallback)
        {
            super(capacity, finalCallback);
        }

        public void tick(float progressIncrement, Storage<FluidVariant> output, Storage<ItemVariant> itemOutput, TransactionContext transaction)
        {
            if (recipe != null)
            {
                progress += progressIncrement;
                if (progress >= totalProgress)
                {
                    StorageWrapper wrapper = new StorageWrapper(this, output, itemOutput);

                    if (recipe.takeInputs(wrapper, transaction))
                    {
                        recipe.ejectOutputs(wrapper, transaction);
                    }
                    recipe = null;
                    progress = 0;
                }
            }
            else
            {
                StorageWrapper wrapper = new StorageWrapper(this, output, itemOutput);
                recipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.TROMMEL, wrapper).orElse(null);
            }
        }

        @Override
        public void writeNbt(NbtCompound nbt)
        {
            super.writeNbt(nbt);
            nbt.putFloat("progress", progress);
        }

        @Override
        public NbtCompound readNbt(NbtCompound nbt)
        {
            super.readNbt(nbt);
            this.progress = nbt.getFloat("progress");
            return nbt;
        }
    }

    private record StorageWrapper(SingleVariantStorage<FluidVariant> input, Storage<FluidVariant> output, Storage<ItemVariant> itemOutput) implements TrommelStorage
    {
    }
}
