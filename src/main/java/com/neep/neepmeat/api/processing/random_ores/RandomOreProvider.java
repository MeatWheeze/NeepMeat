package com.neep.neepmeat.api.processing.random_ores;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class RandomOreProvider
{
    private final Random random;
    private final WeightedMap<RandomOres.Entry> entryMap = new WeightedMap<>();

    public RandomOreProvider(World world, BlockPos pos, Random random, List<RandomOres.Entry> entries)
    {
        this.random = random;

        for (var entry : entries)
        {
            entryMap.put(entry.modifiedWeight(world, pos), entry);
        }
    }

    public List<ItemStack> random(ServerWorld world, BlockPos origin)
    {
        float p = random.nextFloat();
        RandomOres.Entry entry = entryMap.get(random::nextInt, p);
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
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(origin))
                    .add(LootContextParameters.TOOL, Items.NETHERITE_PICKAXE.getDefaultStack())
                    .addOptional(LootContextParameters.THIS_ENTITY, null)
                    .addOptional(LootContextParameters.BLOCK_ENTITY, null);
            return entry.state().getDroppedStacks(builder);
        }
    }
}
