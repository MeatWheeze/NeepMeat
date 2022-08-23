package com.neep.neepmeat.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;

public class NMFeatures
{
//    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BLOOD_BUBBLE_TREE = ConfiguredFeatures.register("blood_bubble_tree", Feature.TREE,
//            new TreeFeatureConfig.Builder(BlockStateProvider.of(Blocks.OAK_LOG),
//                    new BendingTrunkPlacer(4, 2, 0, 3, UniformIntProvider.create(1, 2)),
//                    new WeightedBlockStateProvider((DataPool<BlockState>) DataPool.builder()
//                            .add((BlockState) Blocks.AZALEA_LEAVES.getDefaultState(), 3)
//                            .add((BlockState) Blocks.FLOWERING_AZALEA_LEAVES.getDefaultState(), 1)),
//                    new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50),
//                    new TwoLayersFeatureSize(1, 0, 1)).dirtProvider(BlockStateProvider.of(Blocks.ROOTED_DIRT)).forceDirt().build());


    public static void init()
    {

    }
}
