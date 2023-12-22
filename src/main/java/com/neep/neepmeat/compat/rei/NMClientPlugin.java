package com.neep.neepmeat.compat.rei;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.neepmeat.compat.rei.category.*;
import com.neep.neepmeat.compat.rei.display.*;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
import com.neep.neepmeat.recipe.AlloyKilnRecipe;
import com.neep.neepmeat.recipe.EnlighteningRecipe;
import com.neep.neepmeat.recipe.GrindingRecipe;
import com.neep.neepmeat.recipe.PressingRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.reason.DisplayAdditionReason;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NMClientPlugin implements REIClientPlugin, NMREIPlugin
{
    private static final Comparator<MeatRecipe<?>> RECIPE_COMPARATOR = Comparator.comparing((MeatRecipe<?> o) -> o.getId().getNamespace()).thenComparing(o -> o.getId().getPath());
    private List<MeatRecipe<?>> sortedRecipes = null;
    DisplayAdditionReason SPECIAL_RECIPE_MANAGER = DisplayAdditionReason.simple();

    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        registerRecipeFiller(registry, GrindingRecipe.class, NMrecipeTypes.GRINDING, GrindingDisplay::new);
        registry.registerRecipeFiller(MixingRecipe.class, NMrecipeTypes.MIXING, MixingDisplay::new);
        registry.registerRecipeFiller(AlloyKilnRecipe.class, NMrecipeTypes.ALLOY_SMELTING, AlloySmeltingDisplay::new);
        registry.registerRecipeFiller(EnlighteningRecipe.class, NMrecipeTypes.ENLIGHTENING, EnlighteningDisplay::new);
        registry.registerRecipeFiller(PressingRecipe.class, NMrecipeTypes.PRESSING, PressingDisplay::new);

        // Charnel Compactor recipes
        int page = 0;
        UnmodifiableIterator<List<Item>> iterator = Iterators.partition(Registry.ITEM.getEntryList(NMTags.RAW_MEAT).orElseThrow().stream().map(entry -> entry.value()).iterator(), 35);
        while (iterator.hasNext())
        {
            List<Item> entries = iterator.next();
            registry.add(CompactingDisplay.of(entries, Collections.singletonList(EntryIngredients.of(new ItemStack(NMItems.CRUDE_INTEGRATION_CHARGE))), page++));
        }

        // Heart extraction
        registry.add(HeartExtractionDisplay.of(List.of(EntityType.ZOMBIE), Collections.singletonList(EntryIngredients.of(new ItemStack(NMItems.ANIMAL_HEART)))));
    }

    @Override
    public void registerCategories(CategoryRegistry registry)
    {
        registry.add(
                new GrindingCategory(),
                new CompactingCategory(),
                new MixingCategory(),
                new AlloyKilnCategory(),
                new HeartExtractionCategory(),
                new EnlighteningCategory(),
                new PressingCategory()
        );

        registry.addWorkstations(GRINDING, EntryStacks.of(NMBlocks.GRINDER.asItem()));
        registry.addWorkstations(COMPACTING, EntryStacks.of(NMBlocks.CHARNEL_COMPACTOR.asItem()));
        registry.addWorkstations(MIXING, EntryStacks.of(NMBlocks.MIXER.asItem()));
        registry.addWorkstations(ALLOY_SMELTING, EntryStacks.of(NMBlocks.ALLOY_KILN.asItem()));
        registry.addWorkstations(HEART_EXTRACTION, EntryStacks.of(NMItems.SACRIFICIAL_DAGGER.asItem()));
        registry.addWorkstations(ENLIGHTENING, EntryStacks.of(NMBlocks.PEDESTAL.asItem()));
        registry.addWorkstations(PRESSING, EntryStacks.of(NMBlocks.HYDRAULIC_PRESS.asItem()));
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage)
    {
        // Inject all special recipes
        this.sortedRecipes = MeatRecipeManager.getInstance().values().parallelStream().sorted(RECIPE_COMPARATOR).collect(Collectors.toList());
        for (int i = sortedRecipes.size() - 1; i >= 0; i--)
        {
            MeatRecipe<?> recipe = sortedRecipes.get(i);
            DisplayRegistry.getInstance().addWithReason(recipe, SPECIAL_RECIPE_MANAGER);
        }
    }

    public static <T extends MeatRecipe<?>, D extends Display> void registerRecipeFiller(DisplayRegistry registry, Class<T> typeClass, MeatRecipeType<? super T> recipeType, Function<? extends T, @Nullable D> filler) {
        registerRecipeFiller(registry, typeClass, type -> Objects.equals(recipeType, type), Predicates.alwaysTrue(), filler);
    }

    public static <T extends MeatRecipe<?>, D extends Display> void registerRecipeFiller(DisplayRegistry registry, Class<T> typeClass, Predicate<MeatRecipeType<? super T>> type, Predicate<? extends T> predicate, Function<? extends T, @Nullable D> filler)
    {
        registry.registerFiller(typeClass, recipe -> type.test((MeatRecipeType<? super T>) recipe.getType()) && ((Predicate<T>) predicate).test(recipe), filler);
    }
}