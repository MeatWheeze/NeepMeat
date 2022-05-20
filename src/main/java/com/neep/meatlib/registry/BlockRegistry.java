package com.neep.meatlib.registry;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.block.IMeatBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockRegistry
{
    public static final Map<Identifier, Block> BLOCKS = new LinkedHashMap<>();

    public static Block queueBlock(IMeatBlock block)
    {
        if (!(block instanceof Block))
        {
            throw new IllegalArgumentException("tried to queue something that wasn't a block.");
        }

        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), (Block) block);
        return (Block) block;
    }

    public static Block queueBlock(Block block, String registryName)
    {
        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
        return block;
    }

    public static void registerBlocks()
    {
        for (Map.Entry<Identifier, Block> entry : BLOCKS.entrySet())
        {
            Registry.register(Registry.BLOCK, entry.getKey(), entry.getValue());
        }
    }
}
