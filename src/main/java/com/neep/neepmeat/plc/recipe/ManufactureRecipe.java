package com.neep.neepmeat.plc.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ManufactureRecipe implements MeatRecipe<MutateInPlace<ItemStack>>
{
    protected final Identifier id;
    protected final Item base;
    protected final Item output;
    protected List<ManufactureStep<ItemStack>> steps;

    public ManufactureRecipe(Identifier id, Item base, Item output, List<ManufactureStep<ItemStack>> steps)
    {
        this.id = id;
        this.base = base;
        this.output = output;
        this.steps = steps;
    }

    @Override
    public boolean matches(MutateInPlace<ItemStack> context)
    {
        ItemStack stack = context.get();

        if (!stack.isOf(base))
            return false;

        var workpiece = NMComponents.WORKPIECE.maybeGet(stack).orElse(null);
        if (workpiece != null)
        {
            var workSteps = workpiece.getSteps();

            if (workSteps.size() != steps.size())
                return false;

            for (int i = 0; i < workSteps.size(); ++i)
            {
                var step = workSteps.get(i);

                if (!ManufactureStep.equals(step, steps.get(i)))
                {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean takeInputs(MutateInPlace<ItemStack> context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public boolean ejectOutputs(MutateInPlace<ItemStack> context, TransactionContext transaction)
    {
        context.set(new ItemStack(output));
        return false;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return PLCRecipes.MIXING;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return PLCRecipes.MANUFACTURE_SERIALISER;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public static class Serialiser implements MeatRecipeSerialiser<ManufactureRecipe>
    {
        @Override
        public ManufactureRecipe read(Identifier id, JsonObject json)
        {
            JsonObject baseElement = JsonHelper.getObject(json, "base");
            String idString = JsonHelper.getString(baseElement, "id");
            Identifier baseId = Identifier.tryParse(idString);
            Item base = Registry.ITEM.get(baseId);

            JsonArray stepElement = JsonHelper.getArray(json, "steps");

            List<ManufactureStep<ItemStack>> steps = Lists.newArrayList();
            for (var element : stepElement)
            {
                JsonObject object = JsonHelper.asObject(element, element.toString());

                String stepIdString = JsonHelper.getString(object, "id");
                Identifier stepId = Identifier.tryParse(stepIdString);
                if (stepId == null)
                    throw new JsonParseException("Invalid step ID: " + stepIdString);

                var provider = ManufactureStep.REGISTRY.get(stepId);

                if (provider == null)
                    throw new JsonParseException("Unknown step ID: " + stepIdString);

                // TODO: possibly check types
                ManufactureStep<ItemStack> step = (ManufactureStep<ItemStack>) provider.create(object);

                steps.add(step);
            }

            JsonObject outputObject = JsonHelper.getObject(json, "output");
            Identifier outputId = Identifier.tryParse(JsonHelper.getString(outputObject, "resource"));
            Item output = Registry.ITEM.get(outputId);

            return new ManufactureRecipe(id, base, output, steps);
        }

        @Override
        public ManufactureRecipe read(Identifier id, PacketByteBuf buf)
        {
            // TODO: Implement
            return new ManufactureRecipe(id, Items.DIRT, Items.DIRT, Collections.emptyList());
        }

        @Override
        public void write(PacketByteBuf buf, ManufactureRecipe recipe)
        {

        }
    }
}
