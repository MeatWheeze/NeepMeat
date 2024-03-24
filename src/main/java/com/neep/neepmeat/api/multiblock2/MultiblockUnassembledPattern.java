package com.neep.neepmeat.api.multiblock2;

import com.neep.neepmeat.api.big_block.BigBlockPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiblockUnassembledPattern extends BigBlockPattern
{
    public boolean test(World world, BlockPos controllerPos)
    {
        BlockPos.Mutable mutable = controllerPos.mutableCopy();
        for (var entry : entries())
        {
            mutable.set(controllerPos, entry.key());

            // Ignore the controller
            if (mutable.equals(controllerPos))
                continue;

            if (!world.getBlockState(mutable).equals(entry.value()))
                return false;
        }
        return true;
    }
}
