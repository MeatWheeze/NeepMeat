package com.neep.meatlib.block;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;

import java.util.function.Consumer;

public class PaintedCorrugatedAsbestosBlock extends PaintedBlockManager.PaintedBlock implements MeatlibBlock
{
    public PaintedCorrugatedAsbestosBlock(String registryName, DyeColor col, Settings settings)
    {
        super(registryName, col, settings);
    }

    @Override
    protected BlockItem makeItem()
    {
        return new BaseBlockItem(this, registryName, ItemSettings.block(), new MeatlibItemSettings().tags(NMTags.PAINTED_CORRUGATED_ASBESTOS).group(NMItemGroups.GENERAL));
    }

    @Override
    public void generateRecipe(Consumer<RecipeJsonProvider> exporter)
    {
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, "_dyeing", this, DyeItem.byColor(getCol()), NMTags.PAINTED_CORRUGATED_ASBESTOS);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, this, DyeItem.byColor(getCol()), NMBlocks.CORRUGATED_ASBESTOS);
    }
}
