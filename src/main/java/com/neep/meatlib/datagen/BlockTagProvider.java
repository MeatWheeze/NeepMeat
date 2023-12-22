package com.neep.meatlib.datagen;

import com.neep.meatlib.block.BaseWallBlock;
import com.neep.meatlib.block.IMeatBlock;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        for (Map.Entry<Identifier, Block> entry : BlockRegistry.REGISTERED_BLOCKS.entrySet())
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
