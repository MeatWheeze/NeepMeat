package com.neep.neepmeat.compat.rei;

import com.neep.neepmeat.compat.rei.category.GrindingCategory;
import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.GrindingRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class NMClientPlugin implements REIClientPlugin, NMREIPlugin
{
    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        registry.registerRecipeFiller(GrindingRecipe.class, NMrecipeTypes.GRINDING, GrindingDisplay::new);
    }

    @Override
    public void registerCategories(CategoryRegistry registry)
    {
        registry.add(
                new GrindingCategory()
        );

        registry.addWorkstations(GRINDING, EntryStacks.of(NMBlocks.GRINDER.asItem()));
    }
}
