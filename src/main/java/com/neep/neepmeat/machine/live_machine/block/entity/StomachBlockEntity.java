package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.process.BioreactorProcess;
import com.neep.neepmeat.recipe.BioreactorRecipe;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.input.Input;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class StomachBlockEntity extends BlockEntity implements LivingMachineComponent
{
    private final InputSlot slot = new InputSlot();

    public StomachBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        slot.writeNbt(nbt);
        super.writeNbt(nbt);
    }

    @Override
    public ComponentType<? extends LivingMachineComponent> getComponentType()
    {
        return LivingMachineComponents.STOMACH;
    }

    public InputSlot getSlot()
    {
        return slot;
    }

    public static class InputSlot implements NbtSerialisable
    {
        private float counter;

        @Nullable private BioreactorRecipe recipe;

        public void tickRecipe(float progressIncrement, BioreactorProcess.Context context)
        {
            if (recipe == null)
            {
                recipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.BIOREACTOR, context).orElse(null);
                if (recipe != null)
                {
                    counter = recipe.getProcessTime();
                }
            }

            if (recipe != null)
            {
                if (counter > 0)
                {
                    counter = Math.max(0, counter - progressIncrement);
                }
                else
                {
                    try (Transaction transaction = Transaction.openOuter())
                    {
                        if (recipe.takeInputs(context, transaction) && recipe.ejectOutputs(context, transaction))
                            transaction.commit();
                        else
                            transaction.abort();

                        recipe = null;
                    }
                }
            }
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt)
        {
            recipe = MiscUtil.ifPresentOrNull(nbt, "recipe",
                    s -> (BioreactorRecipe) MeatlibRecipes.getInstance().get((Identifier.tryParse(s))).orElse(null));

            return nbt;
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {
            if (recipe != null)
                nbt.putString("recipe", recipe.getId().toString());
        }
    }
}