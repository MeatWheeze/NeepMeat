package com.neep.meatweapons.datagen;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.transport.FluidTransport;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class MWRecipeGenerator
{
    public static void init()
    {

    }

    protected static void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
    }

    static
    {
        MeatRecipeProvider.addSubsidiary(MWRecipeGenerator::generateRecipes);
    }
}
