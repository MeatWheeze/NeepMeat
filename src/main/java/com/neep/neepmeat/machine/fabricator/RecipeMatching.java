package com.neep.neepmeat.machine.fabricator;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public class RecipeMatching
{
    static boolean findMatching(int batchSize, Storage<ItemVariant> input, List<Ingredient> ingredients, List<ItemVariant> takenResources, TransactionContext transaction)
    {
        for (StorageView<ItemVariant> view : input)
        {
            // Recipe has been matched fully
            if (ingredients.isEmpty())
                return true;

            // Ignore empty views
            if (view.isResourceBlank() || view.getAmount() <= 0)
                continue;

            ItemVariant resource = view.getResource();

            var it = ingredients.iterator();
            while (it.hasNext())
            {
                Ingredient ingredient = it.next();

                if (ingredient.isEmpty())
                {
                    it.remove();
                    continue;
                }

                if (ingredient.test(view.getResource().toStack()))
                {
                    // maxAmount = batchSize because crafting recipes only take 1 item for each ingredient.
                    long extracted = view.extract(resource, batchSize, transaction);
                    if (extracted == batchSize)
                    {
                        takenResources.add(view.getResource());

                        // Remove the ingredient once it is satisfied
                        it.remove();
                    }
                }
            }
        }
        return ingredients.isEmpty();
    }

    public static class IngredientAmount
    {
        public final Ingredient ingredient;
        public int amount;

        public IngredientAmount(Ingredient ingredient, int amount)
        {
            this.ingredient = ingredient;
            this.amount = amount;
        }
    }
}
