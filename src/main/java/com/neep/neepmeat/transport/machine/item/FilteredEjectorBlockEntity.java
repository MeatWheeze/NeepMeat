package com.neep.neepmeat.transport.machine.item;

import com.neep.meatweapons.item.filter.FilterList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class FilteredEjectorBlockEntity extends EjectorBlockEntity
{
    private final FilterList filterList = new FilterList();

    public FilteredEjectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        super.serverTick();
    }
}
