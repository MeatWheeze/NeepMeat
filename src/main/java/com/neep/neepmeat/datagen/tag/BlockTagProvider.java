package com.neep.neepmeat.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    protected static List<Pair<TagKey<Block>, Block>> TAGS = new ArrayList<>();

    public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

//    public BlockTagProvider(FabricDataOutput dataGenerator)
//    {
//        super(dataGenerator);
//    }

//    @Override
//    protected void generateTags()
//    {
//    }

    public static void addToTag(TagKey<Block> id, Block block)
    {
        TAGS.add(new Pair<>(id, block));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        for (Pair<TagKey<Block>, Block> pair : TAGS)
        {
            this.getOrCreateTagBuilder(pair.getLeft()).add(pair.getRight());
        }
    }
}
