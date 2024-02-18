package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class EncasedConduitBlockEntity extends VascularConduitBlockEntity
{
//    private BlockState camoState = Blocks.AIR.getDefaultState();
    private BlockState camoState = NMBlocks.SCAFFOLD_PLATFORM.getDefaultState();
//    private BlockState camoState = NMBlocks.RUSTY_VENT.getDefaultState();

    public EncasedConduitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BlockState getCamoState()
    {
        return camoState;
    }

    public void setCamoState(BlockState camoState)
    {
        this.camoState = camoState;
    }
}
