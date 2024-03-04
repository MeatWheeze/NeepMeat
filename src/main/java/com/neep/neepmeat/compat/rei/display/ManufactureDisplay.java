package com.neep.neepmeat.compat.rei.display;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.recipe.ImplantStep;
import com.neep.neepmeat.plc.recipe.InjectStep;
import com.neep.neepmeat.plc.recipe.ItemManufactureRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.SimpleDisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;

public class ManufactureDisplay implements Display
{
    private final List<ManufactureStep<?>> steps;
    private final List<EntryIngredient> outputs;
    private final List<EntryIngredient> inputs;
    private final Item base;

    public ManufactureDisplay(ItemManufactureRecipe recipe)
    {
        this.steps = recipe.getSteps();
        this.outputs = Collections.singletonList(EntryIngredients.ofItems(List.of(recipe.getRecipeOutput().resource()), (int) recipe.getRecipeOutput().minAmount()));
        this.base = (Item) recipe.getBase();

        this.inputs = Lists.newArrayList();
        inputs.add(EntryIngredients.of(base));
        appendStepIngredients(steps, inputs);
    }

    public ManufactureDisplay(Item base, List<ManufactureStep<?>> steps, List<EntryIngredient> output)
    {
        this.base = base;
        this.steps = steps;
        this.outputs = output;

        this.inputs = Lists.newArrayList();
        inputs.add(EntryIngredients.of(base));
        appendStepIngredients(steps, inputs);
    }

    private static void appendStepIngredients(List<ManufactureStep<?>> steps, List<EntryIngredient> ingredients)
    {
        for (var step : steps)
        {
            if (step instanceof CombineStep combineStep)
            {
                ingredients.add(EntryIngredients.of(combineStep.getItem()));
            }
            else if (step instanceof InjectStep injectStep)
            {
                ingredients.add(EntryIngredients.of(injectStep.getFluid()));
            }
            else if (step instanceof ImplantStep implantStep)
            {
                ingredients.add(EntryIngredients.of(implantStep.getItem()));
            }
        }
    }

    @Override
    public List<EntryIngredient> getInputEntries()
    {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries()
    {
        return outputs;
    }

    public List<ManufactureStep<?>> getSteps()
    {
        return steps;
    }

    public Item getBase()
    {
        return base;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.MANUFACTURE;
    }

    public static DisplaySerializer<ManufactureDisplay> getSerializer()
    {
        return null;
    }

    private class Serializer implements SimpleDisplaySerializer<ManufactureDisplay>
    {
        @Override
        public NbtCompound save(NbtCompound tag, ManufactureDisplay display)
        {
            tag.put("output", EntryIngredients.save(getOutputIngredients(display)));
            tag.putString("base", Registry.ITEM.getId(base).toString());
            NbtList steps = new NbtList();

            for (var step : display.getSteps())
            {
                NbtCompound stepNbt = new NbtCompound();
                stepNbt.putString("id", step.getId().toString());
                stepNbt.put("step", step.toNbt());
            }

            tag.put("steps", steps);
            return tag;
        }

        @Override
        public NbtCompound saveExtra(NbtCompound tag, ManufactureDisplay display)
        {
            return tag;
        }

        @Override
        public ManufactureDisplay read(NbtCompound tag)
        {
            List<EntryIngredient> output = EntryIngredients.read(tag.getList("output", NbtElement.COMPOUND_TYPE));

            Item base = Registry.ITEM.get(Identifier.tryParse(tag.getString("base")));

            List<ManufactureStep<?>> steps = Lists.newArrayList();
            NbtList nbtSteps = tag.getList("steps", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < nbtSteps.size(); ++i)
            {
                NbtCompound stepNbt = nbtSteps.getCompound(i);
                Identifier id = Identifier.tryParse(stepNbt.getString("id"));
                if (id == null)
                    throw new IllegalStateException();

                ManufactureStep<?> step = ManufactureStep.REGISTRY.get(id).create(stepNbt.getCompound("step"));
                steps.add(step);
            }

            return new ManufactureDisplay(base, steps, output);
        }
    }
}
