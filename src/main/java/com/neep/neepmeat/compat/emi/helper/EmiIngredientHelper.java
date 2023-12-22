package com.neep.neepmeat.compat.emi.helper;

import com.neep.meatlib.recipe.ingredient.RecipeInput;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class EmiIngredientHelper {
    public static final Identifier FLUID = new Identifier("minecraft", "fluid");
    public static final Identifier ITEM = new Identifier("minecraft", "item");

    public static final EmiIngredient EMPTY = EmiIngredient.of(Ingredient.EMPTY);
    public static final List<EmiIngredient> EMPTY_LIST = List.of(EMPTY);

    public static List<EmiIngredient> inputsToIngredients(DefaultedList<RecipeInput<?>> inputs) {
        if (inputs.isEmpty()) return EMPTY_LIST;

        if (inputs.size() == 1) {
            RecipeInput<?> input = inputs.get(0);
            if (input.isEmpty()) return EMPTY_LIST;

            return inputToIngredient(input);
        }

        List<List<EmiIngredient>> result = new ArrayList<>(inputs.size());

        boolean emptyFlag = true;
        for (int i = inputs.size() - 1; i >= 0; i--) {
            RecipeInput<?> input = inputs.get(i);

            if (emptyFlag && input.isEmpty()) continue;

            result.add(0, inputToIngredient(input));
            emptyFlag = false;
        }

        return result.stream().flatMap(List::stream).toList(); // TODO: !?
    }

    public static List<EmiIngredient> inputToIngredient(RecipeInput<?> input) {
        if (input.isEmpty()) return EMPTY_LIST;

        if (ITEM.equals(input.getType())) {
            List<Item> items = (List<Item>) input.getAll();
            if (items.isEmpty()) return EMPTY_LIST;

            return items.stream().map(item -> (EmiIngredient) EmiStack.of(item, input.amount())).toList();
        }

        if (FLUID.equals(input.getType())) {
            List<Fluid> fluids = (List<Fluid>) input.getAll();
            if (fluids.isEmpty()) return EMPTY_LIST;

            return fluids.stream().map(fluid -> (EmiIngredient) EmiStack.of(fluid, input.amount())).toList();
        }

        return null; // TODO: uh oh
    }
}
