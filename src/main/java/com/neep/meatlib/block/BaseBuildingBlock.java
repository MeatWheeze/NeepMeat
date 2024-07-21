package com.neep.meatlib.block;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class BaseBuildingBlock extends Block implements MeatlibBlock
{
    private final Item blockItem;

    public final MeatlibBlock slab;
    public final MeatlibBlock stairs;
    public MeatlibBlock wall = null;

    public BaseBuildingBlock(RegistrationContext ctx, boolean makeWall, Settings settings)
    {
        this(ctx, makeWall, ItemSettings.block(), settings);
    }

    public BaseBuildingBlock(RegistrationContext ctx, boolean makeWall, ItemSettings itemSettings, Settings settings)
    {
        super(settings);

        this.stairs = new BaseStairsBlock(ctx, this.getDefaultState(), ItemSettings.block(), settings);

        this.slab = new BaseSlabBlock(ctx, this.getDefaultState(), ItemSettings.block(), settings);

        if (makeWall)
        {
            wall = new BaseWallBlock(ctx, ItemSettings.block(), settings);
        }

        this.blockItem = itemSettings.create(this, ctx, ItemSettings.block());

        ctx.append(this, s -> s + "_stairs", stairs);
        ctx.append(this, s -> s + "_slab", slab);
        ctx.append(this, s -> s + "_wall", wall);
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