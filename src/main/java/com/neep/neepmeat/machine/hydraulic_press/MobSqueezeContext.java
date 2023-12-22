package com.neep.neepmeat.machine.hydraulic_press;

import com.neep.meatlib.recipe.ImplementedRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobSqueezeContext implements ImplementedRecipe.DummyInventory
{
    private final World world;
    private final BlockPos pos;

    public MobSqueezeContext(World world, BlockPos pos)
    {
        this.world = world;
        this.pos = pos;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
