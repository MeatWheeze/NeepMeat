package com.neep.neepmeat.compat.rei.display;

import com.google.common.collect.ImmutableList;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.recipe.surgery.GeneralSurgeryRecipe;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.function.Function;

@Deprecated
public class SurgeryDisplay extends BasicDisplay implements SimpleGridMenuDisplay
{
    private GeneralSurgeryRecipe recipe;

    public SurgeryDisplay(GeneralSurgeryRecipe recipe)
    {
        super(inputsToEntries(recipe.getInputs()),
                Collections.singletonList(EntryIngredients.ofItems(List.of(recipe.getIOutput().resource()), (int) recipe.getIOutput().minAmount())));
        this.recipe = recipe;
    }

    public SurgeryDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
    }

    // I had no idea what I was doing when I made this.
    // AAAAAAAAAAAAAAAAAAAAAAAa

    public static List<EntryIngredient> inputsToEntries(DefaultedList<RecipeInput<?>> inputs)
    {
        if (inputs.size() == 0) return Collections.emptyList();
        if (inputs.size() == 1)
        {
            RecipeInput<?> input = inputs.get(0);
            if (input.isEmpty()) return Collections.emptyList();
            return Collections.singletonList(entryFromInput(input));
        }
        boolean emptyFlag = true;
        List<EntryIngredient> result = new ArrayList<>(inputs.size());
        for (int i = inputs.size() - 1; i >= 0; i--)
        {
            RecipeInput<?> input = inputs.get(i);
            if (emptyFlag && input.isEmpty()) continue;
            result.add(0, entryFromInput(input));
            emptyFlag = false;
        }
        return ImmutableList.copyOf(result);
    }
    // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaa
    public static final Map<Identifier, EntryType<?>> ENTRY_TYPE_MAP = new HashMap<>();

    public static final Map<Identifier, Function<RecipeInput<?>, EntryIngredient>> ENTRY_STACK_MAP = new HashMap<>();
    // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa

    public static <T> EntryIngredient entryFromInput(RecipeInput<T> input)
    {
        if (input.isEmpty()) return EntryIngredient.empty();
       Collection<T> matching = input.getAll();
        if (matching.size() == 0) return EntryIngredient.empty();
//        Function<?, ?> entryFunction = ENTRY_STACK_MAP.get(input.getType());
        return ENTRY_STACK_MAP.get(input.getType()).apply(input);
    }

    public static Serializer<SurgeryDisplay> getSerializer()
    {
        return Serializer.ofSimple(SurgeryDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.SURGERY;
    }

    public static int getSlotWithSize(DefaultCraftingDisplay<?> display, int index, int craftingGridWidth)
    {
        return getSlotWithSize(display.getInputWidth(craftingGridWidth, 3), index, craftingGridWidth);
    }

    public static int getSlotWithSize(int recipeWidth, int index, int craftingGridWidth)
    {
        int x = index % recipeWidth;
        int y = (index - x) / recipeWidth;
        return craftingGridWidth * y + x;
    }

    public List<InputIngredient<EntryStack<?>>> getInputIngredients(int w, int h)
    {
        int inputWidth = getInputWidth(w, h);
        int inputHeight = getInputHeight(w, h);

        Map<IntIntPair, InputIngredient<EntryStack<?>>> grid = new HashMap<>();

        List<EntryIngredient> inputEntries = getInputEntries();
        for (int i = 0; i < inputEntries.size(); i++)
        {
            EntryIngredient stacks = inputEntries.get(i);
            if (stacks.isEmpty())
            {
                continue;
            }
            int index = getSlotWithSize(inputWidth, i, w);
            grid.put(new IntIntImmutablePair(i % inputWidth, i / inputWidth), InputIngredient.of(index, stacks));
        }

        List<InputIngredient<EntryStack<?>>> list = new ArrayList<>(w * h);
        for (int i = 0, n = w * h; i < n; i++)
        {
            list.add(InputIngredient.empty(i));
        }

        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                InputIngredient<EntryStack<?>> ingredient = grid.get(new IntIntImmutablePair(x, y));
                if (ingredient != null) {
                    int index = w * y + x;
                    list.set(index, ingredient);
                }
            }
        }

        return list;
    }

    @Override
    public int getWidth()
    {
        return recipe.getWidth();
    }

    @Override
    public int getHeight()
    {
        return recipe.getHeight();
    }

    static
    {
        ENTRY_TYPE_MAP.put(RecipeInputs.FLUID_ID, VanillaEntryTypes.FLUID);
        ENTRY_STACK_MAP.put(RecipeInputs.ITEM_ID, input ->
        {
            return EntryIngredients.ofItems((Collection<ItemConvertible>) input.getAll(), (int) input.amount());
        });
        ENTRY_TYPE_MAP.put(RecipeInputs.ITEM_ID, VanillaEntryTypes.ITEM);
        ENTRY_STACK_MAP.put(RecipeInputs.FLUID_ID, input ->
        {
            Collection<Fluid> fluids = (Collection<Fluid>) input.getAll();
            EntryIngredient.Builder result = EntryIngredient.builder(fluids.size());
            for (Fluid fluid : fluids)
            {
                result.add(EntryStacks.of(fluid, input.amount()));
            }
            return result.build();

        });
    }

}
