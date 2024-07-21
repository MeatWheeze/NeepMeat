package com.neep.meatlib.block;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;

import java.util.function.Consumer;

public class SmoothTileBlock extends PaintedBlockManager.PaintedBlock implements MeatlibBlock
{
    public SmoothTileBlock(String registryName, DyeColor col, Settings settings)
    {
        super(registryName, col, settings);
    }

    @Override
    protected BlockItem makeItem()
    {
        return new Item(this, registryName, ItemSettings.block());
    }

    public void generateRecipe(Consumer<RecipeJsonProvider> exporter)
    {
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, "_dyeing", this, DyeItem.byColor(getCol()), NMTags.SMOOTH_TILE);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, this, DyeItem.byColor(getCol()), Blocks.SMOOTH_STONE);
    }

    private static class Item extends BaseBlockItem
    {
        public Item(Block block, String registryName, ItemSettings itemSettings)
        {
            super(block, ctx, itemSettings, new MeatlibItemSettings().tags(NMTags.SMOOTH_TILE).group(NMItemGroups.BUILDING));
        }
    }
}
