package com.neep.meatlib.block;

import com.google.common.collect.Lists;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PaintedBlockManager<T extends PaintedBlockManager.PaintedBlock>
{
    public static final List<PaintedBlock> COLOURED_BLOCKS = new ArrayList<>();
    public final List<T> entries = Lists.newArrayList();

    public PaintedBlockManager(RegistrationContext ctx, String name, Constructor<T> constructor, AbstractBlock.Settings settings)
    {
        for (DyeColor col : DyeColor.values())
        {
            T block = ctx.addParent(new Identifier(ctx.namespace(), name + "_" + col.getName()), constructor.create(ctx, col, settings));
//            T block = BlockRegistry.queue(constructor.create(registryName + "_" + col.getName(), col, settings));
            COLOURED_BLOCKS.add(block);
            entries.add(block);
        }
    }

    @FunctionalInterface
    public interface Constructor<T extends PaintedBlock>
    {
        T create(RegistrationContext ctx, DyeColor col, AbstractBlock.Settings settings);
    }

    public abstract static class PaintedBlock extends Block implements MeatlibBlock
    {
        public final BlockItem blockItem;
        public final DyeColor col;

        public PaintedBlock(RegistrationContext ctx, DyeColor col, Settings settings)
        {
            super(settings);
            this.blockItem = makeItem(ctx);
            this.col = col;
        }

        protected abstract BlockItem makeItem(RegistrationContext ctx);

        public DyeColor getCol()
        {
            return this.col;
        }

        public int getRawCol()
        {
            return col.getFireworkColor();
        }

        public void generateRecipe(Consumer<RecipeJsonProvider> exporter)
        {

        }
    }
}
