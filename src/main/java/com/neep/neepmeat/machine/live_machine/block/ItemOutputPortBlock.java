package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.machine.live_machine.block.entity.ItemOutputPortBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

import java.util.function.Supplier;

public class ItemOutputPortBlock extends PortBlock<ItemOutputPortBlockEntity>
{
    public static final BooleanProperty AUTO_EJECT = BooleanProperty.of("auto_eject");

    public ItemOutputPortBlock(String registryName, ItemSettings itemSettings, Supplier<BlockEntityType<ItemOutputPortBlockEntity>> factory, Settings settings)
    {
        super(registryName, itemSettings, factory, settings);
        setDefaultState(getDefaultState().with(AUTO_EJECT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(AUTO_EJECT);
    }
}
