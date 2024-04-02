package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.live_machine.TestLivingMachineBE;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.recipe.GrindingRecipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CrusherSegmentBlockEntity extends SyncableBlockEntity implements LivingMachineComponent
{
    private final InputSlot slot = new InputSlot(this::sync);

    public CrusherSegmentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        slot.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        slot.readNbt(nbt);
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

    @Override
    public ComponentType<?> getComponentType()
    {
        return LivingMachineComponents.CRUSHER_SEGMENT;
    }

    public InputSlot getStorage()
    {
        return slot;
    }

    public static class InputSlot extends WritableStackStorage
    {
        @Nullable
        private GrindingRecipe recipe;
        private float progress;

        public InputSlot(@Nullable Runnable parent)
        {
            super(parent);
        }

        public void tick(float progressIncrement, Storage<ItemVariant> output, TransactionContext transaction)
        {
            var storage = new TestLivingMachineBE.SimpleCrushingStorage(this, output);
            if (recipe != null)
            {
                progress = progress + progressIncrement;
                if (progress >= recipe.getTime())
                {
                    if (recipe.takeInputs(storage, transaction))
                    {
                        recipe.ejectOutputs(storage, transaction);
                    }
                    recipe = null;
                    progress = 0;
                    syncIfPossible();
                }
            }
            else if (!isEmpty())
            {
                GrindingRecipe foundRecipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.ADVANCED_CRUSHING, storage).orElse(null);

                if (foundRecipe == null)
                    foundRecipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.GRINDING, storage).orElse(null);

                if (foundRecipe != null)
                {
                    recipe = foundRecipe;
                }
                else if (!isEmpty())
                {
                    recipe = MeatlibRecipes.getInstance().get(NMrecipeTypes.ADVANCED_CRUSHING, new Identifier(NeepMeat.NAMESPACE, "advanced_crushing/destroy")).orElse(null);
                }
                syncIfPossible();
            }
        }

        @Override
        public long getCapacity()
        {
            return 1;
        }

        @Override
        protected long getCapacity(ItemVariant variant)
        {
            return 1;
        }

        @Override
        public void writeNbt(NbtCompound nbt)
        {
            if (recipe != null)
                nbt.putString("recipe", recipe.getId().toString());
            super.writeNbt(nbt);
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {
            super.readNbt(nbt);
            if (nbt.contains("recipe"))
                this.recipe = MeatlibRecipes.getInstance().get(NMrecipeTypes.GRINDING, Identifier.tryParse(nbt.getString("recipe"))).orElse(null);
            else
                this.recipe = null;
        }

        @Nullable
        public GrindingRecipe getRecipe()
        {
            return recipe;
        }
    }
}
