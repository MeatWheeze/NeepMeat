package com.neep.neepmeat.api.block;

import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.init.BlockInitialiser;
import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.DyeColor;

public class BasePaintedBlock
{
    public BaseBlockItem blockItem;
    private String registryName;

//    public static List<Integer> COLOURS = List.of(
//            0x1D1D21, 0xB02E26, 0x5E7C16, 0x835432,
//            0x3C44AA, 0x8932B8, 0x169C9C, 0x9D9D97,
//            0x474F52, 0xF38BAA, 0x80C71F, 0xFED83D,
//            0x3AB3DA, 0xC74EBD, 0xF9801D, 0xF9FFFE
//    );

//    public static List<String> COLOURS = List.of(


    public BasePaintedBlock(String registryName, AbstractBlock.Settings settings)
    {
        for (DyeColor col : DyeColor.values())
        {
            PaintedBlock block = new PaintedBlock(registryName + "_" + col.getName(), col, settings);
            NeepMeatClient.COLOURED_BLOCKS.add(block);
        }
    }

    public static class PaintedBlock extends Block implements NMBlock
    {
        private final String registryName;
        public BlockItem blockItem;
        public final DyeColor col;

        public PaintedBlock(String registryName, DyeColor col, Settings settings)
        {
            super(settings);
            this.registryName = registryName;
            this.blockItem = new BaseBlockItem(this, registryName, 64, false);
            this.col = col;
            BlockInitialiser.BLOCKS.put(registryName, this);
        }

        @Override
        public String getRegistryName()
        {
            return registryName;
        }

        public int getColour()
        {
            return col.getFireworkColor();
        }
    }
}
