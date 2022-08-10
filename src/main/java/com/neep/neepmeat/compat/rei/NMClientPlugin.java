package com.neep.neepmeat.compat.rei;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.neep.neepmeat.compat.rei.category.CompactingCategory;
import com.neep.neepmeat.compat.rei.category.GrindingCategory;
import com.neep.neepmeat.compat.rei.category.MixingCategory;
import com.neep.neepmeat.compat.rei.display.CompactingDisplay;
import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import com.neep.neepmeat.compat.rei.display.MixingDisplay;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.mixer.MixingRecipe;
import com.neep.neepmeat.recipe.GrindingRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;

public class NMClientPlugin implements REIClientPlugin, NMREIPlugin
{
    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        registry.registerRecipeFiller(GrindingRecipe.class, NMrecipeTypes.GRINDING, GrindingDisplay::new);
        registry.registerRecipeFiller(MixingRecipe.class, NMrecipeTypes.MIXING, MixingDisplay::new);

        int page = 0;
        UnmodifiableIterator<List<Item>> iterator = Iterators.partition(Registry.ITEM.getEntryList(NMTags.CHARNEL_COMPACTOR).orElseThrow().stream().map(entry -> entry.value()).iterator(), 35);
        while (iterator.hasNext())
        {
            List<Item> entries = iterator.next();
            registry.add(CompactingDisplay.of(entries, Collections.singletonList(EntryIngredients.of(new ItemStack(NMItems.CRUDE_INTEGRATION_CHARGE))), page++));
        }
    }

    @Override
    public void registerCategories(CategoryRegistry registry)
    {
        registry.add(
                new GrindingCategory(),
                new CompactingCategory(),
                new MixingCategory()
        );

        registry.addWorkstations(GRINDING, EntryStacks.of(NMBlocks.GRINDER.asItem()));
        registry.addWorkstations(COMPACTING, EntryStacks.of(NMBlocks.CHARNEL_COMPACTOR.asItem()));
        registry.addWorkstations(MIXING, EntryStacks.of(NMBlocks.MIXER.asItem()));
    }
}
