package com.neep.neepmeat.machine.well_head;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class RandomOres
{
    private static final Random random = new Random();
    private static final List<Entry> ENTRIES = Lists.newArrayList();
    @Nullable private static Block[] MATCHING = null;

    public static void register(TagKey<Block> tag)
    {
        ENTRIES.add(new Entry(tag));
    }

    public static List<ItemStack> random(ServerWorld world, BlockPos origin)
    {
        Block[] matching = getMatching();

        Block block;
        if (matching.length > 0)
        {
            block = matching[random.nextInt(matching.length)];
        }
        else
            return Collections.emptyList();

        Identifier identifier = block.getLootTableId();
        if (identifier == LootTables.EMPTY)
        {
            return Collections.emptyList();
        }
        else
        {
            BlockState state = block.getDefaultState();
//
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(origin))
                    .add(LootContextParameters.TOOL, Items.NETHERITE_PICKAXE.getDefaultStack())
                    .addOptional(LootContextParameters.THIS_ENTITY, null)
                    .addOptional(LootContextParameters.BLOCK_ENTITY, null);
            return state.getDroppedStacks(builder);
        }
    }

    public static void init()
    {
        register(ConventionalBlockTags.ORES);
    }

    private static Block[] getMatching()
    {
        if (MATCHING == null)
        {
            Predicate<Block> tags = ENTRIES.stream()
                    .map(Entry::tag)
                    .<Predicate<Block>>map(tag -> (block -> block.getRegistryEntry().isIn(tag)))
                    .reduce(Predicate::or)
                    .orElse(b -> false);
            MATCHING = Registries.BLOCK.stream().filter(tags).toArray(Block[]::new);
        }
        return MATCHING;
    }

    static
    {
        init();
    }

    public record Entry(TagKey<Block> tag)
    {
    }
}
