package com.neep.meatlib.block;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.Registries.BLOCKRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class BaseBuildingBlock extends Block implements MeatlibBlock
{
    BaseBlockItem blockItem;
    String registryName;

    public final MeatlibBlock slab;
    public final MeatlibBlock stairs;
    public MeatlibBlock wall = null;

    public BaseBuildingBlock(String blockName, boolean makeWall, Settings settings)
    {
        super(settings);

        this.stairs = new BaseStairsBlock(this.getDefaultState(),blockName + "_stairs", ItemSettings.block(), settings);
        BlockRegistry.queue(stairs);

        this.slab = new BaseSlabBlock(this.getDefaultState(),blockName + "_slab", ItemSettings.block(), settings);
        BlockRegistry.queue(slab);

        if (makeWall)
        {
            wall = new BaseWallBlock(blockName + "_wall", ItemSettings.block(), settings);
            BlockRegistry.queue(wall);
        }

        this.registryName = blockName;
        this.blockItem = new BaseBlockItem(this, blockName, ItemSettings.block());
        BlockRegistry.queue(this);

    }

    public String getRegistryName()
    {
        return registryName;
    }

    public void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        MeatRecipeProvider.offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this.slab, this);
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this.slab, this, 2);
        MeatRecipeProvider.offerStairsRecipe(exporter, this.stairs, this);
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this.stairs, this);
        if (wall != null)
        {
            MeatRecipeProvider.offerWallRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this.wall, this);
            RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this.wall, this);
        }
    }
}