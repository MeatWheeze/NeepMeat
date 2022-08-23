package com.neep.neepmeat.block.sapling;

import com.neep.neepmeat.world.NMFeatures;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BloodBubbleTreeGenerator extends SaplingGenerator
{
    @Nullable
    @Override
    protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees)
    {
        return NMFeatures.BLOOD_BUBBLE_TREE;
    }
}
