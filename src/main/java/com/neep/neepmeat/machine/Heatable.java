package com.neep.neepmeat.machine;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public interface Heatable
{
    void setBurning();

    default void updateState(World world, BlockPos pos, BlockState oldState) {};

    void setHeat(float heat);

    static int getFurnaceTickIncrement(float heat)
    {
        return (int) MathHelper.clamp(heat / (60f / 1000), 1, 8);
    }

    BlockApiLookup<Heatable, Void> LOOKUP = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "heatable"),
            Heatable.class, Void.class
    );
}
