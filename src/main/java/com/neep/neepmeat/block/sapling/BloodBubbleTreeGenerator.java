package com.neep.neepmeat.block.sapling;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.apache.commons.lang3.NotImplementedException;


public class BloodBubbleTreeGenerator extends SaplingGenerator
{
    @Override
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees)
    {
        throw new NotImplementedException("Please implement me!");
    }
}
