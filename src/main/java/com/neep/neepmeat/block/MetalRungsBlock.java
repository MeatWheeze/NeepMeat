package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.LadderBlock;
import org.checkerframework.checker.units.qual.C;

public class MetalRungsBlock extends LadderBlock implements MeatlibBlock
{
    public MetalRungsBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        ctx.append(this, itemSettings.create(this, ctx, itemSettings));
    }
}
