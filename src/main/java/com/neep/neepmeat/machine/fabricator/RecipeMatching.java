package com.neep.neepmeat.machine.fabricator;

import net.minecraft.recipe.Ingredient;

public class RecipeMatching
{

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

    enum MatchResult
    {
        ABORT,
        TRUE,
        FALSE;

        public static MatchResult of(boolean b)
        {
            return b ? RecipeMatching.MatchResult.TRUE : FALSE;
        }

        public boolean value()
        {
            return this == TRUE;
        }
    }

    static class FabricatorLoopException extends Exception
    {

    }
}
