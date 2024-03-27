package com.neep.neepmeat.recipe;

import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.IGrinderStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class AdvancedCrushingRecipe extends GrindingRecipe
{
    public AdvancedCrushingRecipe(Identifier id, RecipeInput<Item> itemInput, RecipeOutput<Item> itemOutput, RecipeOutput<Item> extraOutput, float experience, int processTime)
    {
        super(id, itemInput, itemOutput, extraOutput, experience, processTime);
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.ADVANCED_CRUSHING;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.ADVANCED_CRUSHING_SERIALIZER;
    }

    public static class DestroyRecipe extends AdvancedCrushingRecipe
    {
        public DestroyRecipe(Identifier id)
        {
            super(id, RecipeInputs.empty(), RecipeOutput.empty(), RecipeOutput.empty(), 0, 40);
        }

        @Override
        public boolean destroy()
        {
            return true;
        }

        @Override
        public boolean matches(IGrinderStorage inventory)
        {
            return false;
        }

        @Override
        public boolean takeInputs(IGrinderStorage storage, TransactionContext transaction)
        {
            var it = storage.getInputStorage().nonEmptyIterator();
            if (it.hasNext())
            {
                StorageView<ItemVariant> view = it.next();
                view.extract(view.getResource(), 1, transaction);
                return true;
            }
            return false;
        }

        @Override
        public boolean ejectOutputs(IGrinderStorage context, TransactionContext transaction)
        {
            return true;
        }
    }
}
