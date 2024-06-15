package com.neep.neepmeat.api.processing.random_ores;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.neep.meatlib.api.event.DataPackPostProcess;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.mixin.feature.CountPlacementModifierAccessor;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;

public class RandomOres implements SimpleSynchronousResourceReloadListener
{
    public static final RandomOres INSTANCE = new RandomOres();

    private final Map<BlockState, MutableEntry> stateToTempEntry = new HashMap<>();
    private final List<Entry> entries = new ArrayList<>();

    public static void init()
    {
        DataPackPostProcess.EVENT.register(INSTANCE::postProcess);

        Registry.register(WeightModifier.REGISTRY, new Identifier(NeepMeat.NAMESPACE, "biome_list"), BiomeListWeightModifier.CODEC);
        Registry.register(WeightModifier.REGISTRY, new Identifier(NeepMeat.NAMESPACE, "biome_tag"), BiomeTagWeightModifier.CODEC);
    }

    public RandomOreProvider makeProvider(World world, BlockPos pos, Random random)
    {
        return new RandomOreProvider(world, pos, random, entries);
    }

    @Override
    public Collection<Identifier> getFabricDependencies()
    {
        return List.of(
                ResourceReloadListenerKeys.TAGS
        );
    }

    private void postProcess(MinecraftServer server)
    {
        List<MutableEntry> entryList = new ArrayList<>();

        Registry<PlacedFeature> placed = server.getRegistryManager().get(RegistryKeys.PLACED_FEATURE);

        placed.forEach(placedFeature ->
        {
            // Only proceed if the attached feature is an ore feature.
            ConfiguredFeature<?, ?> configuredFeature = placedFeature.feature().value();
            if (configuredFeature.config() instanceof OreFeatureConfig oreFeatureConfig)
            {
                for (var placement : placedFeature.placementModifiers())
                {
                    // Only support CountPlacementModifier since most 'ore' ores use it.
                    if (placement instanceof CountPlacementModifier count)
                    {
                        IntProvider intProvider = ((CountPlacementModifierAccessor) count).getCount();
                        int maxCount = intProvider.getMax();

                        oreFeatureConfig.targets.forEach(t ->
                        {
                            if (t.state.streamTags().anyMatch(NMTags.CHARNEL_PUMP_OUTPUT_ORES::equals))
                            {
                                // Multiply count by size to hopefully get the expected number of ore blocks per chunk.
                                entryList.add(new MutableEntry(t.state, maxCount * oreFeatureConfig.size));
                            }
                        });
                    }
                }
            }
        });

        // Combine identical BlockStates
        for (var entry : entryList)
        {
            MutableEntry inMap = stateToTempEntry.computeIfAbsent(entry.state, b -> new MutableEntry(b, 0));
            inMap.count += entry.count;
        }

        entries.addAll(stateToTempEntry.values().stream().map(
                e -> new Entry(e.state, e.count, e.modifiers)).toList());
    }

    @Override
    public Identifier getFabricId()
    {
        return new Identifier(NeepMeat.NAMESPACE, "random_ores");
    }

    @Override
    public void reload(ResourceManager manager)
    {
        stateToTempEntry.clear();
        entries.clear();

        for (Identifier id : manager.findResources("random_ores", path -> path.getPath().endsWith(".json")).keySet())
        {
            if (manager.getResource(id).isPresent())
            {
                try (InputStream stream = manager.getResource(id).get().getInputStream())
                {
                    Reader reader = new InputStreamReader(stream);
                    JsonElement rootElement = JsonParser.parseReader(reader);
                    JsonObject rootObject = (JsonObject) rootElement;

                    // Read target BlockState, count and an optional list of modifiers.
                    BlockState target = BlockState.CODEC.parse(JsonOps.INSTANCE, JsonHelper.getObject(rootObject, "state"))
                            .resultOrPartial(NeepMeat.LOGGER::error).orElseThrow();

                    int count = JsonHelper.getInt(rootObject, "count");

                    List<WeightModifier> modifiers = parseModifiers(rootObject);

                    MutableEntry entry = new MutableEntry(target, count, modifiers);
                    stateToTempEntry.put(target, entry);
                }
                catch (Exception e)
                {
                    NeepMeat.LOGGER.error("Error while reading random ores " + id.toString(), e);
                }
            }
        }
    }

    private List<WeightModifier> parseModifiers(JsonObject root)
    {
        List<WeightModifier> list = new ArrayList<>();
        if (root.has("modifiers"))
        {
            JsonArray modifiers = JsonHelper.getArray(root, "modifiers");
            for (int i = 0; i < modifiers.size(); ++i)
            {
                JsonObject modifierObject = modifiers.get(i).getAsJsonObject();

                Codec<WeightModifier> codec = WeightModifier.CODEC.dispatch("type", WeightModifier::getCodec, Function.identity());
                WeightModifier weightModifier = codec.parse(JsonOps.INSTANCE, modifierObject)
                        .resultOrPartial(NeepMeat.LOGGER::error).orElseThrow();

                list.add(weightModifier);
            }
        }

        return list;
    }

    public record Entry(BlockState state, float weight, List<WeightModifier> modifiers)
    {
        public float modifiedWeight(World world, BlockPos pos)
        {
            float weight = weight();
            for (var modifier : modifiers)
            {
                weight = modifier.apply(weight, world, pos);
            }
            return weight;
        }
    }

    private static class MutableEntry
    {
        final List<WeightModifier> modifiers;
        final BlockState state;
        int count;

        public MutableEntry(BlockState state, int count)
        {
            this(state, count, new ArrayList<>());
        }

        public MutableEntry(BlockState target, int count, List<WeightModifier> modifiers)
        {
            this.state = target;
            this.count = count;
            this.modifiers = modifiers;
        }
    }

}
