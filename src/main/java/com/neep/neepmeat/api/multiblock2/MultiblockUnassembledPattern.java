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

    public <P extends BigBlockPattern> P rotateY(float degrees)
    {
        MultiblockUnassembledPattern newVolume = new MultiblockUnassembledPattern();

        double s = Math.round(Math.sin(Math.toRadians(degrees)));
        double c = Math.round(Math.cos(Math.toRadians(degrees)));

        stateProviderMap.forEach((offset, state) ->
        {
            int newX = (int) Math.round(offset.getX() * c - offset.getZ() * s);
            int newZ = (int) Math.round(offset.getX() * s + offset.getZ() * c);
            newVolume.set( newX, offset.getY(), newZ, state);
        });

        apiMap.forEach((offset, api) ->
        {
            int newX = (int) Math.round(offset.getX() * c - offset.getZ() * s);
            int newZ = (int) Math.round(offset.getX() * s + offset.getZ() * c);
            newVolume.enableApi(newX, offset.getY(), newZ, api);
        });

        return (P) newVolume;
    }
}
