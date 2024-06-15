package com.neep.neepmeat.machine.well_head;

import com.neep.meatlib.api.event.DataPackPostProcess;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.mixin.feature.CountPlacementModifierAccessor;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RandomOres
{
    public static final RandomOres INSTANCE = new RandomOres();

    private final Random random = new Random();

    private final MapThing<Entry> entryMap = new MapThing<>();

    public static List<ItemStack> random(ServerWorld world, BlockPos origin)
    {
        return INSTANCE.random1(world, origin);
    }

    public static void init()
    {
        DataPackPostProcess.EVENT.register(INSTANCE::postProcess);
    }

    public List<ItemStack> random1(ServerWorld world, BlockPos origin)
    {
        float p = random.nextFloat();
        Entry entry = entryMap.get(random, p);
        if (entry == null)
        {
            NeepMeat.LOGGER.info("How strange: {}", p);
            return List.of();
        }

        Identifier identifier = entry.state().getBlock().getLootTableId();
        if (identifier == LootTables.EMPTY)
        {
            return Collections.emptyList();
        }
        else
        {
//            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
//                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(origin))
//                    .add(LootContextParameters.TOOL, Items.NETHERITE_PICKAXE.getDefaultStack())
//                    .addOptional(LootContextParameters.THIS_ENTITY, null)
//                    .addOptional(LootContextParameters.BLOCK_ENTITY, null);
//            return entry.state().getDroppedStacks(builder);
            BlockState state = block.getDefaultState();
            LootContext.Builder builder = new LootContext.Builder(world);
            builder.parameter(LootContextParameters.TOOL, Items.NETHERITE_PICKAXE.getDefaultStack());
            builder.parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(origin));

            LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
            ServerWorld serverWorld = lootContext.getWorld();
            LootTable lootTable = serverWorld.getServer().getLootManager().getTable(identifier);

            return lootTable.generateLoot(lootContext);
        }
    }

    private void postProcess(MinecraftServer server)
    {
        entryMap.clear();


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
                                // Multiply count by size to hopefully get the expected number of ore blocks.
                                entryList.add(new MutableEntry(t.state, maxCount * oreFeatureConfig.size));
                            }
                        });
                    }
                }
            }
        });

        // Post-process the list
        int totalCount = 0;
        Map<BlockState, MutableEntry> tempMap = new HashMap<>();
        for (var entry : entryList)
        {
            MutableEntry inMap = tempMap.computeIfAbsent(entry.state, b -> new MutableEntry(b, 0));
            inMap.count += entry.count;

            totalCount += entry.count;
        }

        for (var entry : tempMap.values())
        {
            float normalisedCount = (float) entry.count / totalCount;
            entryMap.put(normalisedCount, new Entry(entry.state, normalisedCount));
        }
    }

    public record Entry(BlockState state, float weight)
    {


    }
    private static class MutableEntry
    {

        BlockState state;
        int count;
        public MutableEntry(BlockState state, int count)
        {
            this.state = state;
            this.count = count;
        }

    }

//    public static void main(String[] args)
//    {
//        MapThing<String> mapThing = new MapThing<>();
//
//        mapThing.put(0.25f, "ooer");
//        mapThing.put(0.25f, "ooer1");
////        mapThing.put(0.5f, "ooer");
////        mapThing.put(0.5f, "ooer");
//        mapThing.put(0.35f, "oggins");
//        mapThing.put(0.15f, "eee");
//
//        System.out.println(mapThing.entryMap);
//
//        Random random1 = new Random();
//
//        int nulls = 0;
//        int ooers = 0;
//        int ooers1 = 0;
//        int ogginses = 0;
//        int eees = 0;
//
//        int count = 100000;
//        for (int i = 0; i < count; ++i)
//        {
//            float p = random1.nextFloat();
//            String s = mapThing.get(random1, p);
//
//            if (s == null)
//                nulls++;
//            else if (s.equals("ooer"))
//                ooers++;
//            else if (s.equals("ooer1"))
//                ooers1++;
//            else if (s.equals("oggins"))
//                ogginses++;
//            else if (s.equals("eee"))
//                eees++;
//        }
//
//        System.out.println("null: " + nulls / (float) count);
//        System.out.println("ooer: " + ooers / (float) count);
//        System.out.println("ooer1: " + ooers / (float) count);
//        System.out.println("oggins: " + ogginses / (float) count);
//        System.out.println("eee: " + eees / (float) count);
//    }

    private static class MapThing<T>
    {
        private final NavigableMap<Float, T> entryMap = new TreeMap<>();
        private float weightTotal = 0;

        public void put(float weight, T entry)
        {
            weightTotal += weight;
            entryMap.put(weightTotal, entry);
        }

        public T get(Random random, float p)
        {
            if (entryMap.size() == 0)
                return null;

            if (weightTotal == -1)
            {
                weightTotal = 0;
                for (var entry : entryMap.keySet())
                {
                    weightTotal += entry;
                }
            }

            p *= weightTotal;

            var mapEntry = entryMap.higherEntry(p);

            return mapEntry.getValue();
        }

        public void clear()
        {
            entryMap.clear();
            weightTotal = 0;
        }
    }
}
