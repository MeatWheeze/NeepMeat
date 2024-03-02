package com.neep.neepmeat.world;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;

import java.util.concurrent.CompletableFuture;

public class NMFeatures extends FabricDynamicRegistryProvider
{
    public NMFeatures(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    public static void init()
    {
//        MeatLibDataGen.register(NMFeatures::new);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries)
    {
//        final RegistryWrapper.Impl<Biome> biomeRegistry = registries.getWrapperOrThrow(RegistryKeys.BIOME);
//        entries.add(ExampleBiomes.MODDED_BIOME_KEY, biomeRegistry.getOrThrow(ExampleBiomes.MODDED_BIOME_KEY).value());

        var thing = new TreeFeatureConfig.Builder(
                BlockStateProvider.of(NMBlocks.BLOOD_BUBBLE_LOG),
                new BendingTrunkPlacer(5, 2, 0, 3, UniformIntProvider.create(1, 2)),
                new WeightedBlockStateProvider(DataPool.<BlockState>builder()
                        .add(NMBlocks.BLOOD_BUBBLE_LEAVES.getDefaultState(), 3)
                        .add(NMBlocks.BLOOD_BUBBLE_LEAVES_FLOWERING.getDefaultState(), 1)),
                new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50),
                new TwoLayersFeatureSize(1, 0, 1)).dirtProvider(BlockStateProvider.of(Blocks.ROOTED_DIRT)).forceDirt().build();
        entries.add(RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(NeepMeat.NAMESPACE, "blood_bubble_tree")), new ConfiguredFeature<>(Feature.TREE, thing));
    }

    @Override
    public String getName()
    {
        return NeepMeat.NAMESPACE + " features";
    }
}
