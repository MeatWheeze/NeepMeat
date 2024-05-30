package com.neep.neepmeat.compat.rei.display;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.plc.recipe.ItemManufactureRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.SimpleDisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class ItemManufactureDisplay extends ManufactureDisplay<Item>
{
    private final List<EntryIngredient> outputs;
    private final List<EntryIngredient> inputs;

    public ItemManufactureDisplay(ItemManufactureRecipe recipe)
    {
        super(recipe.getBase(), recipe.getSteps());
        this.outputs = Collections.singletonList(EntryIngredients.ofItems(List.of(recipe.getRecipeOutput().resource()), (int) recipe.getRecipeOutput().minAmount()));

        this.inputs = Lists.newArrayList();
        inputs.add(EntryIngredients.of(recipe.getBase()));
        appendStepIngredients(steps, inputs);
    }

    public ItemManufactureDisplay(Item base, List<ManufactureStep<?>> steps, List<EntryIngredient> output)
    {
        super(base, steps);
        this.outputs = output;

        this.inputs = Lists.newArrayList();
        inputs.add(EntryIngredients.of(base));
        appendStepIngredients(steps, inputs);
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

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.MANUFACTURE;
    }

    public static DisplaySerializer<ItemManufactureDisplay> getSerializer()
    {
        // TODO: Work out why this is null and why it doesn't matter
        return null;
    }

    private class Serializer implements SimpleDisplaySerializer<ItemManufactureDisplay>
    {
        @Override
        public NbtCompound save(NbtCompound tag, ItemManufactureDisplay display)
        {
            tag.put("output", EntryIngredients.save(getOutputIngredients(display)));
            tag.putString("base", Registries.ITEM.getId(ItemManufactureDisplay.this.getBase()).toString());
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
        public NbtCompound saveExtra(NbtCompound tag, ItemManufactureDisplay display)
        {
            return tag;
        }

        @Override
        public ItemManufactureDisplay read(NbtCompound tag)
        {
            List<EntryIngredient> output = EntryIngredients.read(tag.getList("output", NbtElement.COMPOUND_TYPE));

            Item base = Registries.ITEM.get(Identifier.tryParse(tag.getString("base")));

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

            return new ItemManufactureDisplay(base, steps, output);
        }
    }
}
