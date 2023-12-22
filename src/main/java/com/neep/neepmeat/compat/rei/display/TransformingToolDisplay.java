package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.plc.recipe.TransformingToolRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class TransformingToolDisplay implements Display
{
    private final TransformingToolRecipe recipe;

    public TransformingToolDisplay(TransformingToolRecipe recipe)
    {
        this.recipe = recipe;
    }

    public static DisplaySerializer<TransformingToolDisplay> serializer()
    {
        return new Serializer();
    }

    @Override
    public List<EntryIngredient> getInputEntries()
    {
        // Jank!
        return List.of(
                EntryIngredients.of(NMItems.TRANSFORMING_TOOL_BASE),
                EntryIngredients.of(NMFluids.STILL_WORK_FLUID, FluidConstants.BUCKET));
    }

    @Override
    public List<EntryIngredient> getOutputEntries()
    {
        return List.of();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.TRANSFORMING_TOOL;
    }

    public Item getBase()
    {
        return (Item) recipe.getBase();
    }

    public ManufactureStep<?> getStep()
    {
        return recipe.getSteps().get(0);
    }

    static class Serializer implements DisplaySerializer<TransformingToolDisplay>
    {
        @Override
        public NbtCompound save(NbtCompound tag, TransformingToolDisplay display)
        {
            return tag;
        }

        @Override
        public TransformingToolDisplay read(NbtCompound tag)
        {
            return new TransformingToolDisplay(TransformingToolRecipe.getInstance());
        }
    }
}
