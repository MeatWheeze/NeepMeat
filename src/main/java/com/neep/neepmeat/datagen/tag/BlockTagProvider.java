package com.neep.neepmeat.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Pair;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{

    public BlockTagProvider(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void generateTags()
    {
        for (Pair<TagKey<Block>, Block> pair : com.neep.meatlib.datagen.BlockTagProvider.TAGS)
        {
            this.getOrCreateTagBuilder(pair.getLeft()).add(pair.getRight());
        }
    }

}
