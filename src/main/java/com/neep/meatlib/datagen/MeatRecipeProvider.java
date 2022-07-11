package com.neep.meatlib.datagen;

import com.neep.meatlib.block.BaseBuildingBlock;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;

import java.util.function.Consumer;

public class MeatRecipeProvider extends FabricRecipeProvider
{
    public MeatRecipeProvider(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    public static void offerStairsRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input)
    {
        RecipeProvider.createStairsRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        BlockRegistry.BLOCKS.values().stream()
                .filter(block -> block instanceof BaseBuildingBlock)
                .forEach(block -> ((BaseBuildingBlock) block).generateRecipes(exporter));
    }
}
