package com.neep.meatlib.datagen;

import com.neep.meatlib.block.BaseWallBlock;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.Registries.BLOCKRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public BlockTagProvider(FabricDataGenerator output)
    {
        super(output);
    }

    @Override
    protected void generateTags()
    {
        for (Map.Entry<Identifier, Block> entry : BlockRegistry.REGISTERED_BLOCKS.entrySet())
        {
            if (entry.getValue() instanceof MeatlibBlock meatBlock)
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
