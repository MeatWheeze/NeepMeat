package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.api.processing.BlockCrushingRegistry;
import com.neep.neepmeat.recipe.BlockCrushingRecipe;
import com.neep.neepmeat.recipe.CrushingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockCrushingDisplay implements Display
{
    private final CategoryIdentifier<? extends BlockCrushingDisplay> category;
    private final BlockCrushingRecipe recipe;
    private final Supplier<Collection<BlockCrushingRegistry.Entry>> entrySupplier;

    public BlockCrushingDisplay(CategoryIdentifier<? extends BlockCrushingDisplay> category, BlockCrushingRecipe recipe, Supplier<Collection<BlockCrushingRegistry.Entry>> entrySupplier)
    {
        this.category = category;
        this.recipe = recipe;
        this.entrySupplier = entrySupplier;
    }

    public static <T extends BlockCrushingRecipe> Function<T, BlockCrushingDisplay> filler(CategoryIdentifier<? extends BlockCrushingDisplay> categoryIdentifier, Supplier<Collection<BlockCrushingRegistry.Entry>> entrySupplier)
    {
        return r -> new BlockCrushingDisplay(categoryIdentifier, r, entrySupplier);
    }

    @Override
    public List<EntryIngredient> getInputEntries()
    {
        return List.of(
                EntryIngredients.ofItems(
                        entrySupplier.get()
                                .stream()
                                .flatMap(e -> e.input().getAll().stream())
                                .collect(Collectors.toUnmodifiableList()),
                        (int) recipe.getBaseAmount())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries()
    {
        EntryIngredient main = EntryIngredient.of(entrySupplier.get().stream().map(entry -> EntryStacks.of(entry.output().resource(), (int) recipe.getBaseAmount())).toList());
        EntryIngredient extra = EntryIngredient.of(entrySupplier.get().stream().map(entry -> EntryStacks.of(entry.extra().resource(), (int) recipe.getExtraAmount())
                .tooltip(Text.of("Chance: " + recipe.getChance()))).toList());
        return List.of(main, extra);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return category;
    }
}
