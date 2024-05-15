package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import net.minecraft.block.AbstractRailBlock;

public abstract class BaseRailBlock extends AbstractRailBlock implements MeatlibBlock
{
    private final String name;

    protected BaseRailBlock(boolean forbidCurves, Settings settings, String name)
    {
        super(forbidCurves, settings);
        this.name = name;
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
