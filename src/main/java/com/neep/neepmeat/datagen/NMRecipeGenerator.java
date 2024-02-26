package com.neep.neepmeat.datagen;

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

public class NMRecipeGenerator
{
    public static void init()
    {

    }

    protected static void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        MeatRecipeProvider.offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, NMItems.MEAT_STEEL, RecipeCategory.BUILDING_BLOCKS, NMBlocks.MEAT_STEEL_BLOCK);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.RUSTY_METAL_BLOCK, Items.DIRT, NMBlocks.POLISHED_METAL);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.DIRTY_RED_TILES, NMItems.BLOOD_BUBBLE, Blocks.TERRACOTTA);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.DIRTY_WHITE_TILES, Blocks.DIRT, Blocks.TERRACOTTA);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.BLUE_IRON_BLOCK, Items.BLUE_DYE, NMBlocks.POLISHED_METAL);

        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.WHITE_ROUGH_CONCRETE, Items.SAND, Blocks.WHITE_CONCRETE);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.GREY_ROUGH_CONCRETE, Items.SAND, Blocks.GRAY_CONCRETE);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.YELLOW_ROUGH_CONCRETE, Items.SAND, Blocks.YELLOW_CONCRETE);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.RED_ROUGH_CONCRETE, Items.SAND, Blocks.RED_CONCRETE);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.BLUE_ROUGH_CONCRETE, Items.SAND, Blocks.BLUE_CONCRETE);

//        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.BLUE_SCAFFOLD, Items.BLUE_DYE, NMTags.METAL_SCAFFOLDING);
//        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.YELLOW_SCAFFOLD, Items.YELLOW_DYE, NMTags.METAL_SCAFFOLDING);


//
//        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.DIRTY_YELLOW_TILES, NMFluids.BLOOD_BUCKET, NMBlocks.YELLOW_TILES);
        for (var pipe : FluidTransport.COLOURED_FLUID_PIPES.values())
        {
            MeatRecipeProvider.offerEightDyeingRecipe(exporter, pipe, DyeItem.byColor(pipe.col.dyeCol), NMTags.FLUID_PIPES);
        }
    }

    static
    {
        MeatRecipeProvider.addSubsidiary(NMRecipeGenerator::generateRecipes);
    }
}
