package com.neep.neepmeat.plc.recipe;

import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.item.TransformingTools;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class TransformingToolRecipe implements ManufactureRecipe<MutateInPlace<ItemStack>>
{
    private static final TransformingToolRecipe INSTANCE = new TransformingToolRecipe();

    public static TransformingToolRecipe getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean matches(MutateInPlace<ItemStack> context)
    {
        ItemStack stack = context.get();

        if (context.get().isOf(NMItems.TRANSFORMING_TOOL_BASE))
        {
            var workpiece = NMComponents.WORKPIECE.getNullable(stack);
            if (workpiece != null)
            {
                if (workpiece.getSteps().size() != 3)
                    return false;

                boolean b0 = workpiece.getSteps().get(0) instanceof CombineStep;
                boolean b1 = workpiece.getSteps().get(1) instanceof CombineStep;
                boolean b2 = workpiece.getSteps().get(2) instanceof InjectStep inject
                        && inject.getFluid().matchesType(NMFluids.STILL_WORK_FLUID);

                return (b0 && b1 && b2);
            }
        }
        return false;
    }

    @Override
    public boolean takeInputs(MutateInPlace<ItemStack> context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public boolean ejectOutputs(MutateInPlace<ItemStack> context, TransactionContext transaction)
    {
        Workpiece workpiece = NMComponents.WORKPIECE.get(context.get());

        ItemVariant variant0 = ((CombineStep) workpiece.getSteps().get(0)).getVariant();
        ItemVariant variant1 = ((CombineStep) workpiece.getSteps().get(1)).getVariant();

        ItemVariant combined = TransformingTools.combine(variant0, variant1);
        if (combined == null)
        {
            return false;
        }

        context.set(combined.toStack(1));
        return true;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return null;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return null;
    }

    @Override
    public Identifier getId()
    {
        return null;
    }

    @Override
    public Object getBase()
    {
        return null;
    }

    @Override
    public List<ManufactureStep<?>> getSteps()
    {
        return null;
    }
}
