package com.neep.assembly.storage;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

import java.util.function.Function;

public class AssemblyContainer extends PalettedContainer<BlockState>
{
    public AssemblyContainer(Palette<BlockState> fallbackPalette, IdList<BlockState> idList, Function<NbtCompound, BlockState> elementDeserializer, Function<BlockState, NbtCompound> elementSerializer, BlockState defaultElement)
    {
        super(fallbackPalette, idList, elementDeserializer, elementSerializer, defaultElement);
    }

    public BlockState get(BlockPos pos)
    {
        return this.get(pos.getX(), pos.getY(), pos.getZ());
    }
}
