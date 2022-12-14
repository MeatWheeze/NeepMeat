package com.neep.meatlib.block;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

import java.util.function.Consumer;

public class BaseBuildingBlock extends Block implements IMeatBlock
{
    BaseBlockItem blockItem;
    String registryName;

    public final IMeatBlock slab;
    public final IMeatBlock stairs;
    public IMeatBlock wall = null;

    public BaseBuildingBlock(String blockName, int itemMaxStack, boolean makeWall, Settings settings)
    {
        super(settings);

        this.stairs = new BaseStairsBlock(this.getDefaultState(),blockName + "_stairs", itemMaxStack, settings);
        BlockRegistry.queue(stairs);

        this.slab = new BaseSlabBlock(this.getDefaultState(),blockName + "_slab", itemMaxStack, settings);
        BlockRegistry.queue(slab);

        if (makeWall)
        {
            wall = new BaseWallBlock(blockName + "_wall", itemMaxStack, settings);
            BlockRegistry.queue(wall);
        }

        this.registryName = blockName;
        this.blockItem = new BaseBlockItem(this, blockName, itemMaxStack, false);
        BlockRegistry.queue(this);

    }

    public String getRegistryName()
    {
        return registryName;
    }

    public void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        MeatRecipeProvider.offerSlabRecipe(exporter, this.slab, this);
        RecipeProvider.offerStonecuttingRecipe(exporter, this.slab, this, 2);
        MeatRecipeProvider.offerStairsRecipe(exporter, this.stairs, this);
        RecipeProvider.offerStonecuttingRecipe(exporter, this.stairs, this);
        if (wall != null)
        {
            MeatRecipeProvider.offerWallRecipe(exporter, this.wall, this);
            RecipeProvider.offerStonecuttingRecipe(exporter, this.wall, this);
        }
    }
}