package com.neep.meatlib.datagen;

import com.neep.meatlib.block.BaseWallBlock;
import com.neep.meatlib.block.IMeatBlock;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Map;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public BlockTagProvider(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void generateTags()
    {
        for (Map.Entry<Identifier, Block> entry : BlockRegistry.BLOCKS.entrySet())
        {
            if (entry.getValue() instanceof IMeatBlock meatBlock)
            {
                this.getOrCreateTagBuilder(meatBlock.getPreferredTool()).add(entry.getValue());
            }

            if (entry.getValue() instanceof BaseWallBlock wall)
            {
                this.getOrCreateTagBuilder(wall.getWallTag()).add(entry.getValue());
            }
        }
    }
}
