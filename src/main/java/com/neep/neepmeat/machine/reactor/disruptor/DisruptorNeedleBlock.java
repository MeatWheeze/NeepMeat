package com.neep.neepmeat.machine.reactor.disruptor;

import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;

public class DisruptorNeedleBlock extends BaseColumnBlock
{
    private static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public DisruptorNeedleBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, ItemSettings.block(), settings);
        setDefaultState(getStateManager().getDefaultState().with(AXIS, Direction.Axis.Y).with(ACTIVE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ACTIVE);
    }
}
