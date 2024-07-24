package com.neep.meatweapons.datagen;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.init.MWBlocks;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class MWRecipeGenerator extends MeatRecipeProvider
{
    public MWRecipeGenerator(FabricDataOutput output)
    {
        super(output);
    }

    public static void init()
    {

    }

    protected static void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        // Barrels
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.IRON_BARREL, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(Items.IRON_INGOT))
                .input('n', Items.IRON_NUGGET)
                .input('i', Items.IRON_INGOT)
                .pattern(" ni")
                .pattern("nin")
                .pattern("in ")
                .offerTo(exporter);
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.MEAT_STEEL_BARREL, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('n', NMItems.MEAT_SCRAP)
                .input('i', NMItems.MEAT_STEEL_INGOT)
                .pattern(" ni")
                .pattern("nin")
                .pattern("in ")
                .offerTo(exporter);

        // Base modules
        new ShapedRecipeJsonBuilder(RecipeCategory.TOOLS, MWItems.MEATGUN_PISTOL, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('n', Items.IRON_NUGGET)
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .input('c', NMItems.CONTROL_UNIT)
                .input('i', NMItems.INTERNAL_COMPONENTS)
                .pattern("nnn")
                .pattern("imm")
                .pattern("nnc")
                .offerTo(exporter);
        new ShapedRecipeJsonBuilder(RecipeCategory.TOOLS, MWItems.MEATGUN_STAFF, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .input('c', NMItems.CONTROL_UNIT)
                .input('i', NMItems.INTERNAL_COMPONENTS)
                .pattern("ic")
                .pattern("m ")
                .pattern("m ")
                .offerTo(exporter);

        // Tinker table
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWBlocks.TINKER_TABLE.asItem(), 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('n', Items.IRON_NUGGET)
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .input('b', NMItems.BIOELECTRIC_ORGAN)
                .input('i', NMItems.INTERNAL_COMPONENTS)
                .input('t', Blocks.CRAFTING_TABLE.asItem())
                .pattern("nnn")
                .pattern("ibi")
                .pattern("mtm")
                .offerTo(exporter);

        // Modules
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.PISTOL, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(Items.IRON_INGOT))
                .input('b', MWItems.IRON_BARREL)
                .input('i', Items.IRON_INGOT)
                .pattern("  ")
                .pattern("bi")
                .pattern(" i")
                .offerTo(exporter);
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.CHUGGER, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .input('b', MWItems.IRON_BARREL)
                .input('i', Items.IRON_NUGGET)
                .pattern("ii")
                .pattern("bm")
                .pattern("mm")
                .offerTo(exporter);
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.GRENADE_LAUNCHER, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .input('b', MWItems.IRON_BARREL)
                .input('i', Items.IRON_NUGGET)
                .input('c', NMItems.CONTRACTILE_ACTUATOR)
                .pattern("ii ")
                .pattern("bbc")
                .pattern("mm ")
                .offerTo(exporter);
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.UNDERBARREL, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(Items.IRON_INGOT))
                .input('m', Items.IRON_INGOT)
                .input('b', NMItems.INTERNAL_COMPONENTS)
                .input('i', Items.IRON_NUGGET)
                .pattern(" i")
                .pattern("bm")
                .pattern("bm")
                .offerTo(exporter);
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.DOUBLE_CAROUSEL, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .input('b', NMItems.INTERNAL_COMPONENTS)
                .input('i', Items.IRON_INGOT)
                .pattern("bi")
                .pattern(" m")
                .pattern("bi")
                .offerTo(exporter);
        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.TRIPLE_CAROUSEL, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .input('b', NMItems.INTERNAL_COMPONENTS)
                .input('c', NMItems.CONTRACTILE_ACTUATOR)
                .input('i', Items.IRON_INGOT)
                .pattern("bic")
                .pattern("cmb")
                .pattern("bic")
                .offerTo(exporter);

        new ShapedRecipeJsonBuilder(RecipeCategory.MISC, MWItems.HALBERD, 1)
                .criterion("has", RecipeProvider.conditionsFromItem(NMItems.MEAT_STEEL_INGOT))
                .input('d', Items.DIAMOND)
                .input('m', NMItems.MEAT_STEEL_INGOT)
                .pattern("dm ")
                .pattern("dmm")
                .pattern("dm ")
                .offerTo(exporter);
    }

    static
    {
        MeatRecipeProvider.addSubsidiary(MWRecipeGenerator::generateRecipes);
    }
}
