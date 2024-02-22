package com.neep.neepmeat.transport.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;

public interface EncasedBlockEntity
{
    BlockState getCamoState();
    void setCamoState(BlockState camoState);
    VoxelShape getCamoShape();
}
