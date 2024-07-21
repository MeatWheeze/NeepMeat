package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import net.minecraft.block.AbstractRailBlock;

public abstract class BaseRailBlock extends AbstractRailBlock implements MeatlibBlock
{
    protected BaseRailBlock(boolean forbidCurves, Settings settings)
    {
        super(forbidCurves, settings);
    }
}
