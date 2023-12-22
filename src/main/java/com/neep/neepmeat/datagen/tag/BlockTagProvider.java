package com.neep.neepmeat.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    protected static List<Pair<TagKey<Block>, Block>> TAGS = new ArrayList<>();

    public BlockTagProvider(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void generateTags()
    {
        for (Pair<TagKey<Block>, Block> pair : TAGS)
        {
            this.getOrCreateTagBuilder(pair.getLeft()).add(pair.getRight());
        }
    }

    public static void addToTag(TagKey<Block> id, Block block)
    {
        TAGS.add(new Pair<>(id, block));
    }
}
