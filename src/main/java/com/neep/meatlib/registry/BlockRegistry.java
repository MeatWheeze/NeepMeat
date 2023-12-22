package com.neep.meatlib.registry;

import com.neep.meatlib.block.NMBlock;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockRegistry
{
    public static final Map<Identifier, NMBlock> BLOCKS = new LinkedHashMap<>();

    public static Block queueBlock(NMBlock block)
    {
        if (!(block instanceof Block))
        {
            throw new IllegalArgumentException("tried to register something that wasn't a block.");
        }

        BLOCKS.put(new Identifier(NeepMeat.NAMESPACE, block.getRegistryName()), block);
        return (Block) block;
    }

    public static void registerBlocks()
    {
        for (Map.Entry<Identifier, NMBlock> entry : BLOCKS.entrySet())
        {
            Registry.register(Registry.BLOCK, entry.getKey(), (Block) entry.getValue());
        }
    }
}
